package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeItem;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EquipmentTypeJdbcRepositoryAdapter implements EquipmentTypeRepository {

    private final EquipmentTypeJdbcRepository delegate;
    private final EquipmentTypeMapper mapper;

    @Override
    public EquipmentType save(EquipmentType equipmentType) {
        EquipmentTypeEntity entity = mapper.map(equipmentType);
        entity = delegate.save(entity);
        return mapper.map(entity).toBuilder()
                .createdBy(equipmentType.createdBy())
                .build();
    }

    @Override
    public Optional<EquipmentType> findById(long id) {
        return delegate.findOneById(id)
                .map(mapper::map);
    }

    @Override
    public Page<EquipmentType> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = delegate.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, delegate::count);
        return new DefaultPage<>(aPage.map(mapper::map));
    }

    @Override
    public List<EquipmentTypeItem> findAllItems(String nameQuery) {
        var pageable = PageRequest.of(0, 50);
        return delegate.findAllByNameContainingIgnoreCase(nameQuery, pageable).getContent();
    }

    @Mapper(config = MappingConfig.class, uses = {BaseMapper.class})
    interface EquipmentTypeMapper {

        @Mapping(source = "createdById", target = "createdBy.id")
        EquipmentType map(EquipmentTypeEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        EquipmentType map(EquipmentTypeJdbcRepository.EquipmentTypeProjection source);

        @Mapping(source = "createdBy.id", target = "createdById")
        EquipmentTypeEntity map(EquipmentType source);

    }

}
