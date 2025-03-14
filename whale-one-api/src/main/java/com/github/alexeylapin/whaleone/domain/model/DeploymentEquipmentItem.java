package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record DeploymentEquipmentItem(
        long deploymentId,
        EquipmentTypeRef equipmentTypeRef,
        EquipmentItem equipmentRef,
        Set<EquipmentAttribute> attributes
) {
}
