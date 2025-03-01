package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentItem;

import java.util.List;

public interface DeploymentEquipmentRepository {

    List<DeploymentEquipmentItem> findAllByDeploymentId(long deploymentId);

    void save(DeploymentEquipment deploymentEquipment);

    void delete(DeploymentEquipment deploymentEquipment);

}
