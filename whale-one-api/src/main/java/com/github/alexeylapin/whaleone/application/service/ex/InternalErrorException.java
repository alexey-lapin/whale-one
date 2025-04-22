package com.github.alexeylapin.whaleone.application.service.ex;

public class InternalErrorException extends ServiceException {

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
