package com.github.alexeylapin.whaleone.domain.model;

public record DeploymentMarker(
        long id,
        Double latitude,
        Double longitude,
        String name
) {
}
