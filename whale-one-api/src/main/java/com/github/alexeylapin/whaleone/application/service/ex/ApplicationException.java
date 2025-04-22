package com.github.alexeylapin.whaleone.application.service.ex;

public class ApplicationException extends ServiceException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
