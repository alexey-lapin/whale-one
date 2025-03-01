package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeAttributeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.JsonValue;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EquipmentTypeAttributeJdbcRepositoryAdapter implements EquipmentTypeAttributeRepository {

    private final EquipmentTypeAttributeJdbcRepository delegate;
    private final EquipmentTypeAttributeMapper mapper;

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
    interface EquipmentTypeAttributeMapper {

        EquipmentTypeAttribute map(EquipmentTypeAttributeEntity source);

        EquipmentTypeAttributeEntity map(EquipmentTypeAttribute source);

        default String map(JsonValue source) {
            if (source == null) {
                return null;
            }
            return source.getValue();
        }

        default JsonValue map(String source) {
            return new JsonValue(source);
        }

    }

}
