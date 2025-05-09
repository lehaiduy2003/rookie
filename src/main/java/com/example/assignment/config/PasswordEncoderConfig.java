package com.example.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration class to set up password encoding.
 * This class configures the password encoder for the application.
 * It provides a bean for the PasswordEncoder to be used for encoding and matching passwords.
 * It uses BCryptPasswordEncoder for secure password hashing.
 */
@Configuration
public class PasswordEncoderConfig {
    /**
     * Bean to configure the password encoder.
     * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
