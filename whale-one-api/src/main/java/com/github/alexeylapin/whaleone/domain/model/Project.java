package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
public record Project(
        long id,
        int version,
        ZonedDateTime createdAt,
        UserRef createdBy,
        ZonedDateTime lastUpdatedAt,
        UserRef lastUpdatedBy,

        String name,
        String client,
        String ownership,
        String region,
        String type,
        String goal,
        String description
) {
}
