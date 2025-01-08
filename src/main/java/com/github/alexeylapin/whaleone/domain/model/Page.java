package com.github.alexeylapin.whaleone.domain.model;

import java.util.List;

public record Page<T>(
        List<T> items,
        int totalPages,
        int totalElements
) {
}
