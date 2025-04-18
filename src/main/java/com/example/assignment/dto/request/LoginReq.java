package com.example.assignment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for login requests.
 * Contains email and password for authentication.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
    private String email;
    private String password;
}