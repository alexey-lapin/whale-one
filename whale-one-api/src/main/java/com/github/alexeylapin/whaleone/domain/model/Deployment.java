package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Optional;

@Builder(toBuilder = true)
public record Deployment(
        long id,
        int version,
        ZonedDateTime createdAt,
        long createdById,
        String createdBy,

        long projectId,
        long siteId,
        String name,
        String description,
        DeploymentStatus status
) {
}
