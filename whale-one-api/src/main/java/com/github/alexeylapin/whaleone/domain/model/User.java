package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record User(
        long id,
        int version,
        ZonedDateTime createdAt,
        UserRef createdBy,
        ZonedDateTime lastUpdatedAt,
        UserRef lastUpdatedBy,

        String username,
        String password,
        boolean enabled,
        Set<String> authorities
) {
}
