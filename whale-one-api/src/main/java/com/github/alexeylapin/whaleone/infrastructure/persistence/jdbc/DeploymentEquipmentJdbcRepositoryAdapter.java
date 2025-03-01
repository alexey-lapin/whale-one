package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentItem;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@RegisterReflection(
        classes = DeploymentEquipmentJdbcRepository.DeploymentEquipmentItemRowMapper.class,
        memberCategories = {
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS
        }
)
public class DeploymentEquipmentJdbcRepositoryAdapter implements DeploymentEquipmentRepository {

    private final DeploymentEquipmentJdbcRepository repository;

    @Override
    public List<DeploymentEquipmentItem> findAllByDeploymentId(long deploymentId) {
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
