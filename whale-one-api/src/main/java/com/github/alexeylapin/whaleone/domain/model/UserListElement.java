package com.github.alexeylapin.whaleone.domain.model;

import java.time.Instant;

public record UserListElement(
        long id,
        int version,
        Instant createdAt,
        long createdById,
        Instant lastUpdatedAt,
        long lastUpdatedById,
        String username,
        boolean enabled
) {
}
