package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentJdbcRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJdbcRepository repository;

    public EquipmentJdbcRepositoryAdapter(EquipmentJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Equipment> findAll() {
        return repository.findAll().stream()
                .map(EquipmentJdbcRepositoryAdapter::fromEntity)
                .toList();
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
        return repository.findAllByDeploymentId(id).stream()
                .map(EquipmentJdbcRepositoryAdapter::fromEntity)
                .toList();
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return repository.findById(id)
                .map(EquipmentJdbcRepositoryAdapter::fromEntity);
    }

    @Override
    public List<Equipment> findAllUnassigned() {
        return repository.findAllByDeploymentIdIsNull().stream()
                .map(EquipmentJdbcRepositoryAdapter::fromEntity)
                .toList();
    }

    @Override
    public Equipment save(Equipment equipment) {
        JdbcEquipmentEntity entity = new JdbcEquipmentEntity();
        entity.setId(equipment.id());
        entity.setName(equipment.name());
        entity.setType(equipment.type());
        entity.setDeploymentId(equipment.deploymentId());
        entity = repository.save(entity);
        return fromEntity(entity);
    }

    private static Equipment fromEntity(JdbcEquipmentEntity entity) {
        return Equipment.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .deploymentId(entity.getDeploymentId())
                .build();
    }

}
