package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDetailsRes {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private String bio;
    private Date dob;
    private String role;
    private Date createdOn;
    private Date updatedOn;
    private String memberTier;
}
