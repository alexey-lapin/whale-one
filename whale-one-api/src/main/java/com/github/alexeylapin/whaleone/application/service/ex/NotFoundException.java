package com.github.alexeylapin.whaleone.application.service.ex;

import lombok.NonNull;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(@NonNull Class<?> entityClass, Object id) {
        this(String.format("Failed to find %s with id=%s", entityClass.getName(), id));
    }

}
