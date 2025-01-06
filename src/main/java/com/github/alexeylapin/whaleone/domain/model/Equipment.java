package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Equipment(
        long id,
        String name,
        int type,
        Long deploymentId
) {
}
