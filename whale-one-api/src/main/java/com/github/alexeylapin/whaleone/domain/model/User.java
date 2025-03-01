package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record User(
        long id,
        int version,
        String username,
        String password,
        boolean enabled,
        Set<String> authorities
) {
}
