package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record Equipment(
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
        Long deploymentId,
        Long assemblyId,
        String metadata,
        Set<EquipmentAssemblyPart> assemblyParts,
        Set<EquipmentAttribute> attributes
) {
}
