package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record EquipmentAttribute(
        long id,
        long equipmentTypeAttributeId,
        String value
) {
}
