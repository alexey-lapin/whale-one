package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.DeploymentRef;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentAttribute;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentStatus;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@RegisterReflection(
        classes = EquipmentJdbcRepository.EquipmentListElementRowMapper.class,
        memberCategories = {
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS
        }
)
public class EquipmentJdbcRepositoryAdapter implements EquipmentRepository {

    private static final EquipmentJdbcRepository.EquipmentListElementRowMapper rowMapper =
            new EquipmentJdbcRepository.EquipmentListElementRowMapper();

    private final JdbcClient jdbcClient;
    private final EquipmentJdbcRepository repository;
    private final EquipmentMapper mapper;

    @Override
    public Equipment save(Equipment equipment) {
        var entity = mapper.map(equipment);
        entity = repository.save(entity);
        return mapper.map(entity).toBuilder()
                .createdBy(equipment.createdBy())
                .lastUpdatedBy(equipment.lastUpdatedBy())
                .type(equipment.type())
                .build();
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return repository.findOneById(id).map(mapper::map);
    }

    @Override
    public Page<EquipmentListElement> list(int page, int size, QuerySpec querySpec) {
        var sql = """
                SELECT e.*,
                       et.name     type_name,
                       u1.username created_by_name,
                       u2.username last_updated_by_name,
                       d.name      deployment_name
                FROM equipment e
                         JOIN equipment_type et on e.type_id = et.id
                         JOIN tbl_user u1 on e.created_by_id = u1.id
                         JOIN tbl_user u2 on e.last_updated_by_id = u2.id
                         LEFT JOIN deployment d on e.deployment_id = d.id
                %s
                ORDER BY et.name, e.manufacturer, e.model, e.name
                LIMIT ? OFFSET ?"""
                .formatted(querySpec.spec())
                .replace(System.lineSeparator(), " ");
        var pageable = PageRequest.of(page, size);
        var items = jdbcClient.sql(sql)
                .params(querySpec.params())
                .param(pageable.getPageSize())
                .param(pageable.getOffset())
                .query(rowMapper)
                .list();
        var dataPage = PageableExecutionUtils.getPage(items, pageable, () -> count(querySpec));
        return new DefaultPage<>(dataPage);
    }

    @Override
    public Page<Equipment> search(int page, int size, Collection<EquipmentTypeAttribute> equipmentTypeAttributes, QuerySpec querySpec) {
        var sql = """
                SELECT e.*,
                       et.name     type_name,
                       u1.username created_by_name,
                       u2.username last_updated_by_name,
                       d.name      deployment_name""";
        for (var equipmentTypeAttribute : equipmentTypeAttributes) {
            sql += ", ea" + equipmentTypeAttribute.id() + ".value ea_" + equipmentTypeAttribute.id();
        }
        sql += """
                         FROM equipment e
                         JOIN equipment_type et on e.type_id = et.id
                         JOIN tbl_user u1 on e.created_by_id = u1.id
                         JOIN tbl_user u2 on e.last_updated_by_id = u2.id
                         LEFT JOIN deployment d on e.deployment_id = d.id
                         %s
                %s
                ORDER BY et.name, e.manufacturer, e.model, e.name
                LIMIT ? OFFSET ?"""
                .formatted(createAttributeJoins(equipmentTypeAttributes), querySpec.spec())
                .replace(System.lineSeparator(), " ");
        var pageable = PageRequest.of(page, size);
        var items = jdbcClient.sql(sql)
                .params(querySpec.params())
                .param(pageable.getPageSize())
                .param(pageable.getOffset())
                .query((rs, rowNum) -> {
                    DeploymentRef deploymentRef;
                    Long deploymentId = rs.getLong("deployment_id");
                    if (rs.wasNull()) {
                        deploymentRef = null;
                    } else {
                        String deploymentName = rs.getString("deployment_name");
                        deploymentRef = new DeploymentRef(deploymentId, deploymentName);
                    }
                    Long assemblyId = rs.getLong("assembly_id");
                    if (rs.wasNull()) {
                        assemblyId = null;
                    }
                    var attributes = new LinkedHashSet<EquipmentAttribute>();
                    for (EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes) {
                        var value = rs.getString("ea_" + equipmentTypeAttribute.id());
                        var attribute = new EquipmentAttribute(-1, equipmentTypeAttribute.id(), value);
                        attributes.add(attribute);
                    }
                    return Equipment.builder()
                            .id(rs.getLong("id"))
                            .createdAt(rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()))
                            .createdBy(new UserRef(rs.getLong("created_by_id"), rs.getString("created_by_name")))
                            .lastUpdatedAt(rs.getTimestamp("last_updated_at").toInstant().atZone(ZoneId.systemDefault()))
                            .lastUpdatedBy(new UserRef(rs.getLong("last_updated_by_id"), rs.getString("last_updated_by_name")))
                            .active(rs.getBoolean("active"))
                            .status(EquipmentStatus.valueOf(rs.getString("status")))
                            .type(new EquipmentTypeRef(rs.getLong("type_id"), rs.getString("type_name")))
                            .name(rs.getString("name"))
                            .manufacturer(rs.getString("manufacturer"))
                            .model(rs.getString("model"))
                            .assemblyId(assemblyId)
                            .deployment(deploymentRef)
                            .attributes(attributes)
                            .build();
                })
                .list();
        var dataPage = PageableExecutionUtils.getPage(items, pageable, () -> count(equipmentTypeAttributes, querySpec));
        return new DefaultPage<>(dataPage);
    }

    @Override
    public long count(QuerySpec querySpec) {
        return count(List.of(), querySpec);
    }

    @Override
    public long count(Collection<EquipmentTypeAttribute> equipmentTypeAttributes, QuerySpec querySpec) {
        var sql = "SELECT count(*) FROM equipment e %s %s"
                .formatted(createAttributeJoins(equipmentTypeAttributes), querySpec.spec())
                .replace(System.lineSeparator(), " ");
        return jdbcClient.sql(sql)
                .params(querySpec.params())
                .query(Long.class)
                .single();
    }

    @Override
    public Optional<EquipmentItem> findItemById(long id) {
        return repository.findItemById(id);
    }

    @Override
    public List<EquipmentItem> findAllItems(long typeId, String nameQuery, boolean includeAllocated) {
        var pageable = PageRequest.of(0, 50);
        return repository.findAllItems(
                pageable.getPageSize(),
                pageable.getOffset(),
                typeId,
                nameQuery,
                includeAllocated);
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
//        return repository.findAllByDeploymentId(id).stream()
//                .map(EquipmentJdbcRepositoryAdapter::map)
//                .toList();
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateDeploymentId(long id, Long deploymentId) {
        repository.updateDeploymentId(id, deploymentId);
    }

    private static String createAttributeJoins(Collection<EquipmentTypeAttribute> equipmentTypeAttributes) {
        return equipmentTypeAttributes.stream()
                .map(a -> ("LEFT JOIN equipment_attribute ea%s on ea%s.equipment_id = e.id " +
                           "and ea%s.equipment_type_attribute_id = %s")
                        .formatted(a.id(), a.id(), a.id(), a.id()))
                .collect(Collectors.joining(" "));
    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        @Mapping(source = "type.id", target = "typeId")
        EquipmentEntity map(Equipment source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "typeId", target = "type.id")
        Equipment map(EquipmentEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        @Mapping(source = "typeId", target = "type.id")
        @Mapping(source = "typeName", target = "type.name")
        Equipment map(EquipmentJdbcRepository.EquipmentProjection source);

    }

}
