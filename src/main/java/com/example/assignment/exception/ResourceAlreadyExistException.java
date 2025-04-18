package com.example.assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException {
    /**
     * Constructs a new ResourceAlreadyExistException with the specified detail message.
     * @param message the detail message
     */
    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
