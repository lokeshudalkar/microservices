package com.jobportal.ApplicationService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String , String>> handleIllegalStateException(IllegalStateException e){
        Map<String , String> response = Map.of("error" , e.getMessage());
        return new ResponseEntity<>(response , HttpStatus.CONFLICT);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String , String>> handleRuntimeException(RuntimeException e){
        Map<String , String> response = Map.of("error" , e.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
}
