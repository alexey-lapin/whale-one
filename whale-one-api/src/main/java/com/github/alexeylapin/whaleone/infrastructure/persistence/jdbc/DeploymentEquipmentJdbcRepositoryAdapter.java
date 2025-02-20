package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class DeploymentEquipmentJdbcRepositoryAdapter implements DeploymentEquipmentRepository {

    private final DeploymentEquipmentJdbcRepository repository;

    @Override
    public List<DeploymentEquipment> findAllByDeploymentId(long deploymentId) {
        return repository.findAllByDeploymentId(deploymentId);
    }

    @Override
    public void save(DeploymentEquipment deploymentEquipment) {
        repository.save(deploymentEquipment.deploymentId(), deploymentEquipment.equipmentId());
    }

    @Override
    public void delete(DeploymentEquipment deploymentEquipment) {
        repository.delete(deploymentEquipment.deploymentId(), deploymentEquipment.equipmentId());
    }

}
