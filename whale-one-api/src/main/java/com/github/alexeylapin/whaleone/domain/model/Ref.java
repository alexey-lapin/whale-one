package com.github.alexeylapin.whaleone.domain.model;

import lombok.NonNull;

public record Ref<ID, T>(
        @NonNull ID id,
        @NonNull T value
) {
}
