package com.jobportal.jobservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle runtime exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
// This method will catch any RuntimeException thrown from your controllers
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        // Create a simple map to hold the error message
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
        // For everything else (NullPointer, DB connection), return 500
        return new ResponseEntity<>(Map.of("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
