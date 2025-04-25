package com.example.assignment.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Custom Authentication Entry Point for handling unauthorized access.
 * This class implements the AuthenticationEntryPoint interface
 * and overrides the commence method to send a 401 Unauthorized response.
 */
@Configuration
public class AuthEntryPointImpl implements AuthenticationEntryPoint {
    private static final Log logger = LogFactory.getLog(AuthEntryPointImpl.class);

    /**
     * Handles unauthorized access attempts.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param arg2 the AuthenticationException object
     * @throws IOException if an error occurs while sending the response
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        logger.info("Pre-authenticated entry point called. Rejecting access");
        response.sendError(401, "Unauthorized");
    }
}
