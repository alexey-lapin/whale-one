package com.github.alexeylapin.whaleone.infrastructure.web.api.error;

import com.github.alexeylapin.whaleone.application.service.ex.ApplicationException;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.application.service.ex.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String FAILED_TO_EXECUTE = "Failed to execute";
    private static final String FAILED_TO_EXECUTE_DB_ACTION = FAILED_TO_EXECUTE + " DBAction";

    private static final Pattern DB_ACTION_EXECUTION_EXCEPTION_PATTERN =
            Pattern.compile(FAILED_TO_EXECUTE + " (.*?)[{(].*");

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
        logException(ex);
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

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<Object> handleDbActionExecutionException(DbActionExecutionException ex,
                                                                   WebRequest request) {
        logException(ex);
        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        body.setTitle("DB Action Execution Exception");
        var message = ex.getMessage();
        if (message == null) {
            body.setDetail(FAILED_TO_EXECUTE_DB_ACTION);
        } else {
            var matcher = DB_ACTION_EXECUTION_EXCEPTION_PATTERN.matcher(message);
            if (matcher.find()) {
                body.setDetail(FAILED_TO_EXECUTE + " " + matcher.group(1));
            } else {
                body.setDetail(FAILED_TO_EXECUTE_DB_ACTION);
            }
        }
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        logException(ex);
        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        body.setTitle("Internal Error Exception");
        body.setDetail(ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private void logException(Exception ex) {
        log.error(ex.getMessage(), ex);
    }

}
