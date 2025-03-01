package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record ProjectSite(
        long id,
        long projectId,
        String name,
        Double longitude,
        Double latitude,
        Double depth
) {
}
