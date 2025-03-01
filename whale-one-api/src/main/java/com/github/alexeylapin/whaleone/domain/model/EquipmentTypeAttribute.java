package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record EquipmentTypeAttribute(
        long id,
        int version,
        long equipmentTypeId,
        String name,
        String description,
        String type,
        String metadata
) {
}
