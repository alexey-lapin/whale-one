package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentJdbcRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJdbcRepository repository;

    public EquipmentJdbcRepositoryAdapter(EquipmentJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = new EquipmentEntity();
        entity.setId(equipment.id());
        entity.setVersion(equipment.version());
        entity.setCreatedAt(equipment.createdAt().toInstant());
        entity.setCreatedById(equipment.createdById());
        entity.setName(equipment.name());
        entity.setType(equipment.type());
        entity.setDeploymentId(equipment.deploymentId());
        entity = repository.save(entity);
        return map(entity).toBuilder()
                .createdBy(equipment.createdBy())
                .build();
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return repository.findOneById(id)
                .map(EquipmentJdbcRepositoryAdapter::map);
    }

    @Override
    public Page<Equipment> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(EquipmentJdbcRepositoryAdapter::map));
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
        return repository.findAllByDeploymentId(id).stream()
                .map(EquipmentJdbcRepositoryAdapter::map)
                .toList();
    }

    private static Equipment map(EquipmentWithUserNameEntity entity) {
        return map((EquipmentEntity) entity).toBuilder()
                .createdBy(entity.getCreatedByName())
                .build();
    }

    private static Equipment map(EquipmentEntity entity) {
        return Equipment.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                .createdById(entity.getCreatedById())
                .name(entity.getName())
                .type(entity.getType())
                .deploymentId(entity.getDeploymentId())
                .build();
    }

}
