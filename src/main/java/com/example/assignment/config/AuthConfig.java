package com.example.assignment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Security configuration class to set up authentication and password encoding.
 * This class configures the authentication manager and password encoder for the application.
 * It also provides a DaoAuthenticationProvider for user authentication.
 */
@Component
@RequiredArgsConstructor
public class AuthConfig {

    private final PasswordEncoder passwordEncoder;

    /**
     * Bean to configure the authentication manager.
     * This bean is used to manage authentication requests.
     * @param authenticationConfiguration the authentication configuration
     * @return an AuthenticationManager instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean to configure the DaoAuthenticationProvider.
     * This provider is used to authenticate users using a database.
     * It uses the UserService to load user details and the PasswordEncoder to encode passwords.
     * @param userService the user service to load user details
     * @return a DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
