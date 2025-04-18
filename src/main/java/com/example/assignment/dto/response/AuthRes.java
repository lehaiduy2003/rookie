package com.example.assignment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Contains user details and JWT token information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRes {
    private UserDetailsRes userDetails;
    private String accessToken;
    private String refreshToken;

    /**
     * Constructor without token fields for backward compatibility
     */
    public AuthRes(UserDetailsRes userDetails) {
        this.userDetails = userDetails;
    }

    /**
     * Constructor with only an access token for refresh token endpoint
     */
    public AuthRes(String accessToken) {
        this.accessToken = accessToken;
    }
}
