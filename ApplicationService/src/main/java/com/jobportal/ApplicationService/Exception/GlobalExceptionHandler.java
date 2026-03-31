package com.jobportal.ApplicationService.Exception;

import feign.FeignException;
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

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadFailed(FileUploadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(AlreadyAppliedException.class)
    public ResponseEntity<String> handleAlreadyApplied(AlreadyAppliedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Handles Job Post not found via Feign
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleNotFound(FeignException.NotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");
    }

    // Handles generic RuntimeExceptions with safe cause checking
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        // Fallback check for messages if you still use generic RuntimeExceptions
        if ("JOB_SERVICE_DOWN".equals(message)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Job service unavailable");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + message);
    }

}
