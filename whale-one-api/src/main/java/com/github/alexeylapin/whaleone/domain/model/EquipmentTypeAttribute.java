package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record EquipmentTypeAttribute(
        long id,
        String name
) {

}
