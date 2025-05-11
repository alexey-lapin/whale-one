package com.github.alexeylapin.whaleone.application.service.impl;

import com.github.alexeylapin.whaleone.application.service.EquipmentService;
import com.github.alexeylapin.whaleone.application.service.ex.ApplicationException;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.domain.DefaultQuerySpec;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentAttribute;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeAttributeRepository;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.FieldDescriptor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import de.siegmar.fastcsv.writer.CsvWriter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultEquipmentService implements EquipmentService {

    private static final String ALIAS = "e";
    private static final Set<FieldDescriptor> COMMON_FIELD_DESCRIPTORS = Set.of(
            FieldDescriptor.builder().name("typeId").type(Long.class).alias(ALIAS).columnName("type_id").build(),
            FieldDescriptor.builder().name("name").type(String.class).alias(ALIAS).columnName("name").build(),
            FieldDescriptor.builder().name("manufacturer").type(String.class).alias(ALIAS).columnName("manufacturer").build(),
            FieldDescriptor.builder().name("model").type(String.class).alias(ALIAS).columnName("model").build(),
            FieldDescriptor.builder().name("status").type(String.class).alias(ALIAS).columnName("status").build(),
            FieldDescriptor.builder().name("active").type(Boolean.class).alias(ALIAS).columnName("active").build()
    );

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeAttributeRepository equipmentTypeAttributeRepository;
    private final RSQLParser rsqlParser;
    private final Function<Set<FieldDescriptor>, RSQLVisitor<String, List<Object>>> filterVisitorFactory;

    public DefaultEquipmentService(@NonNull EquipmentRepository equipmentRepository,
                                   @NonNull EquipmentTypeAttributeRepository equipmentTypeAttributeRepository,
                                   @NonNull RSQLParser rsqlParser,
                                   @NonNull Function<Set<FieldDescriptor>, RSQLVisitor<String, List<Object>>> filterVisitorFactory) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeAttributeRepository = equipmentTypeAttributeRepository;
        this.rsqlParser = rsqlParser;
        this.filterVisitorFactory = filterVisitorFactory;
    }

    @Override
    public Page<EquipmentListElement> list(int page, int size, String filter) {
        QuerySpec querySpec;
        if (filter.isBlank()) {
            querySpec = new DefaultQuerySpec("", List.of());
        } else {
            querySpec = parseQuerySpec(filter, COMMON_FIELD_DESCRIPTORS);
        }

        return equipmentRepository.list(page, size, querySpec);
    }

    @Override
    public Page<Equipment> search(int page, int size, String filter) {
        var typeId = determineTypeId(filter);
        var equipmentTypeAttributes = equipmentTypeAttributeRepository.findAll(typeId).getContent();
        return search(page, size, filter, equipmentTypeAttributes);
    }

    private Page<Equipment> search(int page,
                                   int size,
                                   String filter,
                                   Collection<EquipmentTypeAttribute> equipmentTypeAttributes) {
        var fieldDescriptors = new HashSet<>(COMMON_FIELD_DESCRIPTORS);
        for (EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes) {
            var fieldDescriptor = FieldDescriptor.builder()
                    .name("ea" + equipmentTypeAttribute.id())
                    .type(String.class)
                    .columnName("value")
                    .alias("ea" + equipmentTypeAttribute.id())
                    .build();
            fieldDescriptors.add(fieldDescriptor);
        }

        var querySpec = parseQuerySpec(filter, fieldDescriptors);

        return equipmentRepository.search(page, size, equipmentTypeAttributes, querySpec);
    }

    private DefaultQuerySpec parseQuerySpec(String filter, Set<FieldDescriptor> fieldDescriptors) {
        var node = rsqlParser.parse(filter);
        var params = new ArrayList<>();
        var sql = node.accept(filterVisitorFactory.apply(fieldDescriptors), params);
        return new DefaultQuerySpec("WHERE " + sql, params);
    }

    private long determineTypeId(String filter) {
        var node = rsqlParser.parse(filter);
        var result = new TypeIdVisitorAttributeResult();
        node.accept(TypeIdAttributeVisitor.INSTANCE, result);
        if (result.count != 1) {
            throw new ApplicationException("Filter must contain exactly one type");
        }
        return result.typeId;
    }

    @SneakyThrows
    @Override
    public void export(String filter, OutputStream outputStream) {
        var typeId = determineTypeId(filter);
        var equipmentTypeAttributes = equipmentTypeAttributeRepository.findAll(typeId).getContent().stream()
                .collect(Collectors.toMap(EquipmentTypeAttribute::id, Function.identity()));

        CsvWriter csvWriter = CsvWriter.builder().build(outputStream);
        int page = 0;
        Page<Equipment> aPage;
        writeHeader(csvWriter, equipmentTypeAttributes.values());
        do {
            aPage = search(page++, 1000, filter, equipmentTypeAttributes.values());
            for (var equipment : aPage.getContent()) {
                write(csvWriter, equipment, equipmentTypeAttributes);
            }
        } while (aPage.hasNext());
        csvWriter.flush();
    }

    @Override
    public Equipment toggleActive(long id, UserRef user) {
        var equipment = equipmentRepository.findById(id).orElseThrow(() -> new NotFoundException(Equipment.class, id));

        var activation = !equipment.active();

        if (equipment.assemblyId() != null) {
            throw new ApplicationException("Cannot activate/deactivate equipment that is part of an assembly");
        }

        if (equipment.assemblyParts() != null) {
            for (var assemblyPart : equipment.assemblyParts()) {
                var partEquipment = equipmentRepository.findById(assemblyPart.equipmentId())
                        .orElseThrow(() -> new NotFoundException(Equipment.class, assemblyPart.equipmentId()));
                var updatedPartEquipmentBuilder = partEquipment.toBuilder();
                if (activation) {
                    if (partEquipment.assemblyId() != null) {
                        throw new ApplicationException("Cannot activate equipment with assembly parts "
                                                       + "that are already assigned to another assembly");
                    }
                    if (!partEquipment.active()) {
                        throw new ApplicationException("Cannot activate equipment with assembly parts "
                                                       + "that are not active");
                    }
                    updatedPartEquipmentBuilder.assemblyId(id);
                } else {
                    if (partEquipment.assemblyId() != id) {
                        throw new ApplicationException("Cannot deactivate equipment with assembly parts "
                                                       + "that are not assigned to this assembly");
                    }
                    updatedPartEquipmentBuilder.assemblyId(null);
                }
                equipmentRepository.save(updatedPartEquipmentBuilder.build());
            }
        }

        var updatedEquipment = equipment.toBuilder()
                .lastUpdatedAt(ZonedDateTime.now()).lastUpdatedBy(user)
                .active(activation)
                .build();
        return equipmentRepository.save(updatedEquipment);
    }

    private static void writeHeader(CsvWriter csvWriter,
                                    Collection<EquipmentTypeAttribute> equipmentTypeAttributes) {
        var fields = new ArrayList<String>();
        fields.add("Id");
        fields.add("Type");
        fields.add("Name");
        fields.add("Manufacturer");
        fields.add("Model");
        fields.add("Status");
        fields.add("Active");
        fields.add("Deployment");
        fields.add("AssemblyId");
        for (var equipmentTypeAttribute : equipmentTypeAttributes) {
            fields.add(equipmentTypeAttribute.name());
        }
        csvWriter.writeRecord(fields);
    }

    private static void write(CsvWriter csvWriter,
                              Equipment equipment,
                              Map<Long, EquipmentTypeAttribute> equipmentTypeAttributes) {
        var fields = new ArrayList<String>();
        fields.add(String.valueOf(equipment.id()));
        fields.add(equipment.type().name());
        fields.add(equipment.name());
        fields.add(equipment.manufacturer());
        fields.add(equipment.model());
        fields.add(equipment.status().name());
        fields.add(String.valueOf(equipment.active()));
        fields.add(equipment.deployment() == null ? null : equipment.deployment().name());
        fields.add(equipment.assemblyId() == null ? null : String.valueOf(equipment.assemblyId()));
        for (EquipmentAttribute attribute : equipment.attributes()) {
            var equipmentTypeAttribute = equipmentTypeAttributes.get(attribute.equipmentTypeAttributeId());
            if ("files".equals(equipmentTypeAttribute.type())) {
                fields.add(null);
            } else {
                fields.add(attribute.value());
            }
        }
        csvWriter.writeRecord(fields);
    }

    private static class TypeIdAttributeVisitor implements RSQLVisitor<Void, TypeIdVisitorAttributeResult> {


        private static final TypeIdAttributeVisitor INSTANCE = new TypeIdAttributeVisitor();

        @Override
        public Void visit(AndNode node, TypeIdVisitorAttributeResult param) {
            node.getChildren().forEach(n -> n.accept(this, param));
            return null;
        }

        @Override
        public Void visit(OrNode node, TypeIdVisitorAttributeResult param) {
            node.getChildren().forEach(n -> n.accept(this, param));
            return null;
        }

        @Override
        public Void visit(ComparisonNode node, TypeIdVisitorAttributeResult param) {
            if ("typeId".equals(node.getSelector()) && "==".equals(node.getOperator().getSymbol()) && node.getArguments().size() == 1) {
                param.increment();
                String arg = node.getArguments().getFirst();
                param.setTypeId(Long.parseLong(arg));
            }
            return null;
        }

    }

    @Getter
    private static class TypeIdVisitorAttributeResult {


        private int count;
        @Setter
        private long typeId;

        public void increment() {
            count++;
        }

    }

}
