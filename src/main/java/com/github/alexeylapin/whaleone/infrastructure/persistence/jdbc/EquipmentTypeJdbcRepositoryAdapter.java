package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EquipmentTypeJdbcRepositoryAdapter implements EquipmentTypeRepository {

    private final EquipmentTypeJdbcRepository repository;

    public EquipmentTypeJdbcRepositoryAdapter(EquipmentTypeJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public EquipmentType save(EquipmentType equipmentType) {
        EquipmentTypeEntity entity = new EquipmentTypeEntity();
        entity.setId(equipmentType.id());
        entity.setVersion(equipmentType.version());
        entity.setCreatedAt(equipmentType.createdAt().toInstant());
        entity.setCreatedById(equipmentType.createdById());
        entity.setName(equipmentType.name());
        entity.setAttributes(equipmentType.attributes().stream()
                .map(attribute -> {
                    EquipmentTypeAttributeEntity attributeEntity = new EquipmentTypeAttributeEntity();
                    attributeEntity.setId(attribute.id());
                    attributeEntity.setName(attribute.name());
                    return attributeEntity;
                })
                .toList());
        entity = repository.save(entity);
        return map(entity).toBuilder()
                .createdBy(equipmentType.createdBy())
                .build();
    }

    @Override
    public Optional<EquipmentType> findById(long id) {
        return repository.findOneById(id)
                .map(EquipmentTypeJdbcRepositoryAdapter::map);
    }

    @Override
    public Page<EquipmentType> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(EquipmentTypeJdbcRepositoryAdapter::map));
    }

    private static EquipmentType map(EquipmentTypeWithUserNameEntity entity) {
        return map(((EquipmentTypeEntity) entity)).toBuilder()
                .createdBy(entity.getCreatedByName())
                .build();
    }

    private static EquipmentType map(EquipmentTypeEntity entity) {
        return EquipmentType.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                .createdById(entity.getCreatedById())
                .name(entity.getName())
                .attributes(entity.getAttributes().stream()
                        .map(attributeEntity -> EquipmentTypeAttribute.builder()
                                .id(attributeEntity.getId())
                                .name(attributeEntity.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}
