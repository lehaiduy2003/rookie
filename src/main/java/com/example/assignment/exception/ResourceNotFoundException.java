package com.example.assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a resource is not found.
 * This exception is annotated with @ResponseStatus to return a 404 NOT_FOUND status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}