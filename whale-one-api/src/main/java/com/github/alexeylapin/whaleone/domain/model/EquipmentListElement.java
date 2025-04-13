package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
public record EquipmentListElement(
        long id,
        int version,
        ZonedDateTime createdAt,
        UserRef createdBy,
        ZonedDateTime lastUpdatedAt,
        UserRef lastUpdatedBy,

        boolean active,
        String name,
        EquipmentTypeRef type,
        String manufacturer,
        String model,
        Long assemblyId,
        Long deploymentId
) {
}
