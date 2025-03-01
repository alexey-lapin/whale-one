package com.github.alexeylapin.whaleone.infrastructure.web.api;

import java.util.List;

public record PageDto<T>(
        List<T> items,
        int number,
        int size,
        long totalElements
) {
}
