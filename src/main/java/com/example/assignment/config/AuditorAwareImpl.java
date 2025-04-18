package com.example.assignment.config;

import com.example.assignment.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This class implements the AuditorAware interface to provide the current auditor
 * for auditing purposes.
 * It retrieves the current authenticated user from the security context.
 */
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<User> {

    /**
     * This method retrieves the current auditor (user) from the security context.
     * @return an Optional containing the current user if authenticated, otherwise an empty Optional
     */
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        return Optional.ofNullable((User) authentication.getPrincipal());
    }
}

