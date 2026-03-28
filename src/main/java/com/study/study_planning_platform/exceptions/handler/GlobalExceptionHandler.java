package com.study.study_planning_platform.exceptions.handler;

import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private StandardError buildError(
            HttpStatus status,
            Exception ex,
            HttpServletRequest request
    ) {
        return new StandardError(
                Instant.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleAllExceptions(Exception exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status)
                .body(buildError(status, exception, request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFoundException(
            ResourceNotFoundException exception, HttpServletRequest request) {
            HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(buildError(status, exception, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> validationErrors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Validation error in the fields below",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(buildError(status, exception, request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentials(BadCredentialsException exception , HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(buildError(status, exception, request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDeniedException(AccessDeniedException exception , HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status)
                .body(buildError(status, exception, request));

    }
}
