package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeItem;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.relational.core.conversion.RelationalConverter;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentTypeJdbcRepositoryAdapter implements EquipmentTypeRepository {

    private final JdbcClient jdbcClient;
    private final EquipmentTypeJdbcRepository delegate;
    private final RowMapper<EquipmentTypeJdbcRepository.EquipmentTypeProjection> rowMapper;
    private final EquipmentTypeMapper mapper;

    public EquipmentTypeJdbcRepositoryAdapter(JdbcClient jdbcClient,
                                              EquipmentTypeJdbcRepository delegate,
                                              EquipmentTypeMapper mapper,
                                              RelationalConverter relationalConverter) {
        this.jdbcClient = jdbcClient;
        this.delegate = delegate;
        this.mapper = mapper;
        this.rowMapper = new SimplePropertyRowMapper<>(EquipmentTypeJdbcRepository.EquipmentTypeProjection.class,
                relationalConverter.getConversionService());
    }

    @Override
    public EquipmentType save(EquipmentType equipmentType) {
        var entity = mapper.map(equipmentType);
        entity = delegate.save(entity);
        return mapper.map(entity).toBuilder()
                .createdBy(equipmentType.createdBy())
                .lastUpdatedAt(equipmentType.lastUpdatedAt())
                .build();
    }

    @Override
    public Optional<EquipmentType> findById(long id) {
        return delegate.findOneById(id).map(mapper::map);
    }

    @Override
    public Page<EquipmentType> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = delegate.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, delegate::count);
        return new DefaultPage<>(aPage.map(mapper::map));
    }

    @Override
    public Page<EquipmentType> list(int page, int size, QuerySpec querySpec) {
        var sql = """
                SELECT et.*,
                       u1.username created_by_name,
                       u2.username last_updated_by_name
                FROM equipment_type et
                         JOIN tbl_user u1 on et.created_by_id = u1.id
                         JOIN tbl_user u2 on et.last_updated_by_id = u2.id
                %s
                ORDER BY et.name
                LIMIT ? OFFSET ?"""
                .formatted(querySpec.spec())
                .replace(System.lineSeparator(), " ");
        var pageable = PageRequest.of(page, size);
        var items = jdbcClient.sql(sql)
                .params(querySpec.params())
                .param(pageable.getPageSize())
                .param(pageable.getOffset())
//                .query(EquipmentTypeJdbcRepository.EquipmentTypeProjection.class)
                .query(rowMapper)
                .list();
        var dataPage = PageableExecutionUtils.getPage(items, pageable, () -> count(querySpec));
        return new DefaultPage<>(dataPage.map(mapper::map));
    }

    @Override
    public long count(QuerySpec querySpec) {
        var sql = "SELECT count(*) FROM equipment_type et %s".formatted(querySpec.spec());
        return jdbcClient.sql(sql)
                .params(querySpec.params())
                .query(Long.class)
                .single();
    }

    @Override
    public List<EquipmentTypeItem> findAllItems(String nameQuery) {
        var pageable = PageRequest.of(0, 50);
        return delegate.findAllByNameContainingIgnoreCase(nameQuery, pageable).getContent();
    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentTypeMapper {

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "assembly", target = "isAssembly")
        EquipmentType map(EquipmentTypeEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        @Mapping(source = "assembly", target = "isAssembly")
        EquipmentType map(EquipmentTypeJdbcRepository.EquipmentTypeProjection source);

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        @Mapping(source = "isAssembly", target = "assembly")
        EquipmentTypeEntity map(EquipmentType source);

    }

}
