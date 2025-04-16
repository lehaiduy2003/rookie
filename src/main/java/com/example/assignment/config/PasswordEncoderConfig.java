package com.example.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Configuration class for password encoding.
 * This class provides a bean for encoding passwords using BCrypt.
 * Use the static method passwordEncoder() to get an instance of PasswordEncoder.
 */
@Component
public class PasswordEncoderConfig {
    private PasswordEncoderConfig() {
        // Private constructor to prevent instantiation
    }
    /**
     * Password encoder bean for encoding passwords.
     * @return a PasswordEncoder instance.
     */
     @Bean
     public static PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
     }
}
