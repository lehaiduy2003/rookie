package com.example.assignment.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Custom Access Denied Handler for handling access denied exceptions.
 * This class implements the AccessDeniedHandler interface
 * and overrides the handle method to send a 403 Forbidden response.
 */
@Configuration
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private static final Log logger = LogFactory.getLog(AccessDeniedHandlerImpl.class);
    /**
     * Handles access denied exceptions.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param accessDeniedException the AccessDeniedException object
     * @throws IOException if an error occurs while sending the response
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        logger.info("You do not have permission to access this resource");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write("You do not have permission to access this resource");

    }
}