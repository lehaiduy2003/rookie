package com.example.assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingResourceException extends RuntimeException {
    /**
     * Constructs a new ExistingResourceException with the specified detail message.
     * @param message the detail message
     */
    public ExistingResourceException(String message) {
        super(message);
    }
}
