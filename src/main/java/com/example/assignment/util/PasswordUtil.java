package com.example.assignment.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility class for password encoding and matching.
 * This class provides methods to encode passwords and check if a raw password matches an encoded password.
 * It uses the PasswordEncoder bean for encoding and matching.
 */
@Component
@RequiredArgsConstructor
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder;
    /**
     * Encodes a password using the configured password encoder.
     *
     * @param password the password to encode
     * @return the encoded password
     */
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Checks if a raw password matches an encoded password.
     *
     * @param rawPassword the raw password
     * @param encodedPassword the encoded password
     * @return true if the passwords match, false otherwise
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
