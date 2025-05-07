package com.example.assignment.exception;

import com.example.assignment.dto.response.ErrorRes;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRes> handleAccessDenied(AccessDeniedException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Access Denied")
                .cause("User does not have permission to access this resource")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorRes);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorRes> handleUnauthenticated(UnAuthorizedException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Unauthorized")
                .cause("User is not authenticated")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorRes> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Resource Not Found")
                .cause("The requested resource was not found")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
    }

    @ExceptionHandler(ExistingResourceException.class)
    public ResponseEntity<ErrorRes> handleResourceAlreadyExist(ExistingResourceException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Resource Already Exists")
                .cause("The resource already exists")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorRes);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRes> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Illegal Argument")
                .cause("An illegal argument was provided")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<ErrorRes> handleLazyInitialization(LazyInitializationException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Lazy Initialization Exception")
                .cause("An error occurred while initializing a lazy-loaded entity")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorRes> handleResourceConflict(ResourceConflictException ex) {
        ErrorRes errorRes = ErrorRes.builder()
                .error("Resource Conflict")
                .cause("A conflict occurred with the requested resource")
                .message(ex.getClass().getName() + ": " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorRes);
    }
}
