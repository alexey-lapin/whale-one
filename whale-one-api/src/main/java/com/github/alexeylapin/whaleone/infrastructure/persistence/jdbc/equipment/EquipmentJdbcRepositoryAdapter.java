package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
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

import java.util.List;
import java.util.Optional;

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
                       u2.username last_updated_by_name
                FROM equipment e
                         JOIN equipment_type et on e.type_id = et.id
                         JOIN tbl_user u1 on e.created_by_id = u1.id
                         JOIN tbl_user u2 on e.last_updated_by_id = u2.id
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
    public long count(QuerySpec querySpec) {
        var sql = "SELECT count(*) FROM equipment e %s".formatted(querySpec.spec());
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
