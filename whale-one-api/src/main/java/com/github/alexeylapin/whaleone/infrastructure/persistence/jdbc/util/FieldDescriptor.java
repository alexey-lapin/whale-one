package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record FieldDescriptor(
        @NonNull String name,
        @NonNull Class<?> type,
        String alias,
        @NonNull String columnName
) {

    public String getField() {
        String field = "";
        if (alias != null && !alias.isEmpty()) {
            field = alias + ".";
        }
        return field + columnName;
    }

}
