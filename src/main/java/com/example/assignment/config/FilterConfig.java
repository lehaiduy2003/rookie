package com.example.assignment.config;

import com.example.assignment.filter.JwtAuthenticationFilter;
import com.example.assignment.provider.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Configuration class for filter beans.
 * This class defines the filter beans used in the application.
 */
@Component
public class FilterConfig {
    /**
     * Creates a JwtAuthenticationFilter bean.
     * @param jwtProvider the JwtProvider bean
     * @param userService the UserService bean
     * @return a JwtAuthenticationFilter bean
     */
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsService userService) {
        return new JwtAuthenticationFilter(jwtProvider, userService);
    }
}
