package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record DeploymentEquipmentItem(
        long deploymentId,
        EquipmentTypeRef equipmentTypeRef,
        EquipmentItem equipmentRef
) {
}
