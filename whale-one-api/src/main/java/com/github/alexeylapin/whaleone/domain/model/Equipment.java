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

        boolean active,
        String name,
        EquipmentTypeRef type,
        Long deploymentId,
        Set<EquipmentAttribute> attributes
) {
}
