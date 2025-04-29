package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentElement;

import java.util.List;

public interface DeploymentEquipmentRepository {

    List<DeploymentEquipmentElement> findAllElementsByDeploymentId(long deploymentId);

    void save(DeploymentEquipment deploymentEquipment);

    void delete(long deploymentId, long equipmentId);

}
