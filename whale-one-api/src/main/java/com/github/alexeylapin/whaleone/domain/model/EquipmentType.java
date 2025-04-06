package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
public record EquipmentType(
        long id,
        int version,
        ZonedDateTime createdAt,
        UserRef createdBy,
        ZonedDateTime lastUpdatedAt,
        UserRef lastUpdatedBy,

        String name,
        String description,
        boolean isAssembly,
        String metadata
) {
}
