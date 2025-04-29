package com.github.alexeylapin.whaleone.domain.model;

import java.util.List;

public record DeploymentEquipmentElement(
        long id,
        String name,
        Long assemblyId,
        EquipmentTypeRef type,
        List<DeploymentEquipmentElement> assemblyParts
) {
}
