package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EquipmentTypeJdbcRepositoryAdapter implements EquipmentTypeRepository {

    private final EquipmentTypeJdbcRepository repository;

    public EquipmentTypeJdbcRepositoryAdapter(EquipmentTypeJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EquipmentType> findAll() {
        return repository.findAll().stream()
                .map(entity -> EquipmentType.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .attributes(entity.getAttributes().stream()
                                .map(attributeEntity -> EquipmentTypeAttribute.builder()
                                        .id(attributeEntity.getId())
                                        .name(attributeEntity.getName())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Override
    public Optional<EquipmentType> findById(int id) {
        return repository.findById(id)
                .map(entity -> EquipmentType.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .attributes(entity.getAttributes().stream()
                                .map(attributeEntity -> EquipmentTypeAttribute.builder()
                                        .id(attributeEntity.getId())
                                        .name(attributeEntity.getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }

    @Override
    public EquipmentType save(EquipmentType equipmentType) {
        JdbcEquipmentTypeEntity entity = new JdbcEquipmentTypeEntity();
        entity.setId(equipmentType.id());
        entity.setName(equipmentType.name());
        entity.setAttributes(equipmentType.attributes().stream()
                .map(attribute -> {
                    JdbcEquipmentTypeAttributeEntity attributeEntity = new JdbcEquipmentTypeAttributeEntity();
                    attributeEntity.setId(attribute.id());
                    attributeEntity.setName(attribute.name());
                    return attributeEntity;
                })
                .toList());
        entity = repository.save(entity);
        equipmentType = equipmentType.toBuilder()
                .id(entity.getId())
                .name(entity.getName())
                .attributes(entity.getAttributes().stream()
                        .map(attributeEntity -> EquipmentTypeAttribute.builder()
                                .id(attributeEntity.getId())
                                .name(attributeEntity.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        return equipmentType;
    }

}
