package com.jobportal.jobservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // This method will catch any RuntimeException thrown from your controllers
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        // Create a simple map to hold the error message
        Map<String, String> response = Map.of("trace", ex.getMessage());

        // Return the map as a JSON object with a 400 Bad Request status
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
