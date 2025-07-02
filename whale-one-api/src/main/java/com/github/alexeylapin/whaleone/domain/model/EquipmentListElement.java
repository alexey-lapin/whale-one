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
        EquipmentStatus status,
        String name,
        EquipmentTypeRef type,
        String manufacturer,
        String model,
        DeploymentRef deployment,
        Long assemblyId,
        String assemblyDescriptor
) {
}
