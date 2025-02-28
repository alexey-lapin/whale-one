package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
public record Deployment(
        long id,
        int version,
        ZonedDateTime createdAt,
        UserRef createdBy,

        ProjectItem projectRef,
        ProjectSiteItem projectSiteRef,
        String name,
        String description,
        DeploymentStatus status,
        String platform,
        String providerOrganisations,
        String providerParticipants
) {
}
