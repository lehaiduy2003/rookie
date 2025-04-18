package com.example.assignment.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling cookie operations.
 * Provides methods for adding, clearing, and extracting cookies.
 */
@Component
public class CookieUtil {

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private static final String ENVIRONMENT = System.getProperty("spring.profiles.active");

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private boolean isSecure() {
        return ENVIRONMENT != null && ENVIRONMENT.equals("production");
    }

    /**
     * Adds a refresh token cookie to the response.
     *
     * @param response the HTTP response
     * @param refreshToken the refresh token
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSecure()); // Set secure flag based on environment
        cookie.setPath("/api/v1/auth/refresh"); // Only accessible by auth endpoints
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000)); // Convert milliseconds to seconds
        response.addCookie(cookie);
    }

    /**
     * Clears the refresh token cookie.
     *
     * @param response the HTTP response
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSecure());
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
    }

    /**
     * Extracts the refresh token from the request cookies.
     *
     * @param request the HTTP request
     * @return the refresh token, or null if not found
     */
    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}