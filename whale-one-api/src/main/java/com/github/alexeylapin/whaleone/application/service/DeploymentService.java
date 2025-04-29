package com.github.alexeylapin.whaleone.application.service;

public interface DeploymentService {

    void addEquipment(long deploymentId, long equipmentId);

    void deleteEquipment(long deploymentId, long equipmentId);

}
