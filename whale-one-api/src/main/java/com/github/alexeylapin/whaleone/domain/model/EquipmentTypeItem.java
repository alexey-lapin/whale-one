package com.github.alexeylapin.whaleone.domain.model;

public record EquipmentTypeItem(
        long id,
        String name,
        boolean isAssembly,
        boolean isDeployable
) {
}
