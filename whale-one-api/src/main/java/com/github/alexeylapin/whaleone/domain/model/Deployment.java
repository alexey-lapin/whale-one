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
        String name,
        String description,
        DeploymentStatus status,
        Long jobId,
        Long siteId
) {

    public Optional<Long> getJobId() {
        return Optional.ofNullable(jobId);
    }

    public Optional<Long> getSiteId() {
        return Optional.ofNullable(siteId);
    }

}
