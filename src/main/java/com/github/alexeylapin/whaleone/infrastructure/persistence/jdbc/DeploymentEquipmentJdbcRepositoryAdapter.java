package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeploymentEquipmentJdbcRepositoryAdapter implements DeploymentEquipmentRepository {

    private final DeploymentEquipmentJdbcRepository delegate;

    public DeploymentEquipmentJdbcRepositoryAdapter(DeploymentEquipmentJdbcRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<DeploymentEquipment> findAllByDeploymentId(long deploymentId) {
        return delegate.findAllByDeploymentId(deploymentId);
    }

    @Override
    public void save(DeploymentEquipment deploymentEquipment) {
        delegate.save(deploymentEquipment.deploymentId(), deploymentEquipment.equipmentId());
    }

    @Override
    public void delete(DeploymentEquipment deploymentEquipment) {
        delegate.delete(deploymentEquipment.deploymentId(), deploymentEquipment.equipmentId());
    }

}
