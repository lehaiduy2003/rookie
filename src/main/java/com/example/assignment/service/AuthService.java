package com.example.assignment.service;

import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.request.RegisterReq;
import com.example.assignment.dto.response.AuthRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service interface for handling authentication operations.
 * This interface defines methods for user registration, login, logout, and token refresh.
 */
public interface AuthService {

    /**
     * Registers a new user.
     *
     * @param registerReq the registration request containing user details
     * @param response the HTTP response to set cookies
     * @return the authentication response containing user details and tokens
     */
    AuthRes register(RegisterReq registerReq, HttpServletRequest request, HttpServletResponse response);

    /**
     * Authenticates a user.
     *
     * @param loginReq the login request containing user credentials
     * @param response the HTTP response to set cookies
     * @return the authentication response containing user details and tokens
     */
    AuthRes login(LoginReq loginReq, HttpServletRequest request, HttpServletResponse response);

    /**
     * Logs out the current user.
     *
     * @param response the HTTP response to clear cookies
     */
    void logout(HttpServletResponse response);

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param request the HTTP request containing the refresh token cookie
     * @return the authentication response containing the new access token
     */
    String refreshToken(HttpServletRequest request);
}
