package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeDeploymentAttributeRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EquipmentTypeDeploymentAttributeJdbcRepositoryAdapter implements EquipmentTypeDeploymentAttributeRepository {

    private final EquipmentTypeDeploymentAttributeJdbcRepository delegate;
    private final EquipmentTypeDeploymentAttributeMapper mapper;

    @Override
    public EquipmentTypeAttribute save(EquipmentTypeAttribute equipmentTypeAttribute) {
        var entity = mapper.map(equipmentTypeAttribute);
        var savedEntity = delegate.save(entity);
        return mapper.map(savedEntity);
    }

    @Override
    public Optional<EquipmentTypeAttribute> findById(long id) {
        return delegate.findById(id).map(mapper::map);
    }

    @Override
    public Page<EquipmentTypeAttribute> findAll(long equipmentTypeId) {
        var page = delegate.findByEquipmentTypeId(equipmentTypeId, Pageable.unpaged(Sort.by("id")));
        return new DefaultPage<>(page.map(mapper::map));
    }

    @Override
    public void deleteById(long id) {
        delegate.deleteById(id);
    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentTypeDeploymentAttributeMapper {

        EquipmentTypeAttribute map(EquipmentTypeDeploymentAttributeEntity source);

        EquipmentTypeDeploymentAttributeEntity map(EquipmentTypeAttribute source);

    }

}
