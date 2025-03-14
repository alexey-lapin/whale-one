package com.github.alexeylapin.whaleone.domain.model;

import java.util.Set;

public record DeploymentEquipment(
        long deploymentId,
        long equipmentId,
        Set<EquipmentAttribute> attributes
) {
}
