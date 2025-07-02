package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.model.UserRef;

public interface DeploymentService {

    void addEquipment(long deploymentId, long equipmentId, UserRef userRef);

    void deleteEquipment(long deploymentId, long equipmentId, UserRef userRef);

}
