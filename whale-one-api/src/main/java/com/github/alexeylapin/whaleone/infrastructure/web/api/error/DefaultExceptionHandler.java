package com.github.alexeylapin.whaleone.infrastructure.web.api.error;

import com.github.alexeylapin.whaleone.application.service.ex.ApplicationException;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.application.service.ex.ServiceException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String PROPERTY_CLASSIFICATION = "classification";

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLockingException(OptimisticLockingFailureException ex,
                                                                   WebRequest request) {
        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        body.setTitle("Optimistic Locking Exception");
        body.setDetail("Version mismatch on saving entity");
        body.setProperty(PROPERTY_CLASSIFICATION, "optimistic-locking-failure");
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex,
                                                         WebRequest request) {
        if (ex instanceof NotFoundException) {
            ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
            body.setTitle("Not Found Exception");
            body.setProperty(PROPERTY_CLASSIFICATION, ex.getClassification().orElse("not-found"));
            return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        } else if (ex instanceof ApplicationException) {
            ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
            body.setTitle("Application Exception");
            body.setProperty(PROPERTY_CLASSIFICATION, ex.getClassification().orElse(null));
            return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        body.setTitle("Internal Error Exception");
        body.setProperty(PROPERTY_CLASSIFICATION, ex.getClassification().orElse(null));
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        body.setTitle("Internal Error Exception");
        body.setDetail(ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
