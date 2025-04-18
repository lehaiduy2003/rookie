package com.example.assignment.controller;

import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.request.RegisterReq;
import com.example.assignment.dto.response.AuthRes;
import com.example.assignment.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication requests.
 * This controller provides endpoints for user registration, login, logout, and token refresh.
 * All business logic is delegated to the AuthService.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param registerReq the registration request containing user details
     * @param response the HTTP response to set cookies
     * @return the registration response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthRes> register(
            @Valid @RequestBody RegisterReq registerReq,
            HttpServletResponse response
    ) {
        AuthRes authRes = authService.register(registerReq, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(authRes);
    }

    /**
     * Authenticates a user.
     *
     * @param loginReq the login request containing user credentials
     * @param response the HTTP response to set cookies
     * @return the login response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthRes> login(
            @Valid @RequestBody LoginReq loginReq,
            HttpServletResponse response
    ) {
        AuthRes authRes = authService.login(loginReq, response);
        return ResponseEntity.ok(authRes);
    }

    /**
     * Logs out the current user.
     *
     * @param response the HTTP response to clear cookies
     * @return the logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }

    /**
     * Refreshes the access token using a refresh token from cookies.
     * The refresh token is expected to be present in the request cookies.
     * This endpoint is protected and requires the user to be authenticated.
     *
     * @param request the HTTP request containing the refresh token cookie
     * @return the response containing the new access token
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String accessToken = authService.refreshToken(request);
        return ResponseEntity.ok(accessToken);
    }
}
