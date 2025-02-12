package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
public record EquipmentListElement(
        long id,
        int version,
        ZonedDateTime createdAt,
        long createdById,
        String createdBy,

        String name,
        EquipmentTypeRef type,
        Long deploymentId
) {
}
