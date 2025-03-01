package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeAttributeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.JsonValue;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EquipmentTypeAttributeJdbcRepositoryAdapter implements EquipmentTypeAttributeRepository {

    private static final EquipmentTypeAttributeMapper MAPPER = Mappers.getMapper(EquipmentTypeAttributeMapper.class);

    private final EquipmentTypeAttributeJdbcRepository delegate;

    public EquipmentTypeAttributeJdbcRepositoryAdapter(EquipmentTypeAttributeJdbcRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public EquipmentTypeAttribute save(EquipmentTypeAttribute equipmentTypeAttribute) {
        var entity = MAPPER.map(equipmentTypeAttribute);
        var savedEntity = delegate.save(entity);
        return MAPPER.map(savedEntity);
    }

    @Override
    public Optional<EquipmentTypeAttribute> findById(long id) {
        return delegate.findById(id).map(MAPPER::map);
    }

    @Override
    public Page<EquipmentTypeAttribute> findAll(long equipmentTypeId) {
        var page = delegate.findByEquipmentTypeId(equipmentTypeId, Pageable.unpaged(Sort.by("id")));
        return new DefaultPage<>(page.map(MAPPER::map));
    }

    @Override
    public void deleteById(long id) {
        delegate.deleteById(id);
    }

    @Mapper
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
