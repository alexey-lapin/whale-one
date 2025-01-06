package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Optional;

@Builder(toBuilder = true)
public record Deployment(
        long id,
        String name,
        String description,
        DeploymentStatus status,
        Long jobId,
        Long siteId,
        ZonedDateTime createdAt,
        String createdBy
) {

    public Optional<Long> getJobId() {
        return Optional.ofNullable(jobId);
    }

    public Optional<Long> getSiteId() {
        return Optional.ofNullable(siteId);
    }

}
