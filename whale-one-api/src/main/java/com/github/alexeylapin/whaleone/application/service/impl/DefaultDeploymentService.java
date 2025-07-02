package com.github.alexeylapin.whaleone.application.service.impl;

import com.github.alexeylapin.whaleone.application.service.DeploymentService;
import com.github.alexeylapin.whaleone.application.service.EquipmentService;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentElement;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentAssemblyScope;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class DefaultDeploymentService implements DeploymentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentService equipmentService;
    private final DeploymentEquipmentRepository deploymentEquipmentRepository;

    @Override
    public void addEquipment(long deploymentId, long equipmentId, UserRef userRef) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(Equipment.class, equipmentId));
        if (equipment.assemblyScope() == EquipmentAssemblyScope.SESSION) {
            equipmentService.toggleActive(equipmentId, true, userRef);
        }

        deploymentEquipmentRepository.save(new DeploymentEquipment(deploymentId, equipmentId, Set.of()));
        var deploymentEquipmentElements = deploymentEquipmentRepository.findAllElementsByDeploymentId(deploymentId);
        for (var element : deploymentEquipmentElements) {
            if (element.id() == equipmentId) {
                updateDeploymentId(deploymentId, element);
            }
        }
    }

    @Override
    public void deleteEquipment(long deploymentId, long equipmentId, UserRef userRef) {
        var deploymentEquipmentElements = deploymentEquipmentRepository.findAllElementsByDeploymentId(deploymentId);
        for (var element : deploymentEquipmentElements) {
            if (element.id() == equipmentId) {
                updateDeploymentId(null, element);
            }
        }
        deploymentEquipmentRepository.delete(deploymentId, equipmentId);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(Equipment.class, equipmentId));
        if (equipment.assemblyScope() == EquipmentAssemblyScope.SESSION) {
            equipmentService.toggleActive(equipmentId, false, userRef);
        }
    }

    private void updateDeploymentId(Long deploymentId, DeploymentEquipmentElement deploymentEquipmentElement) {
        equipmentRepository.updateDeploymentId(deploymentEquipmentElement.id(), deploymentId);
        for (DeploymentEquipmentElement element : deploymentEquipmentElement.assemblyParts()) {
            updateDeploymentId(deploymentId, element);
        }
    }

}
