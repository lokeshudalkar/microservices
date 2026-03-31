package com.jobportal.ApplicationService.Exception;

public class AlreadyAppliedException extends RuntimeException{
    public AlreadyAppliedException(String message) {
        super(message);
    }
}
