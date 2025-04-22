package com.github.alexeylapin.whaleone.application.service.ex;

import lombok.With;

import java.util.Optional;

public class ServiceException extends RuntimeException {

    @With
    private String classification;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public Optional<String> getClassification() {
        return Optional.ofNullable(classification);
    }

}
