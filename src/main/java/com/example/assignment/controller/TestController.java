package com.example.assignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for testing JWT authentication.
 * This controller provides a simple endpoint to verify that JWT authentication is working.
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * A simple endpoint that requires authentication.
     * This endpoint can be used to verify that JWT authentication is working.
     *
     * @return a simple message
     */
    @GetMapping("/secured")
    public ResponseEntity<String> securedEndpoint() {
        return ResponseEntity.ok("This is a secured endpoint. If you can see this, JWT authentication is working!");
    }
}