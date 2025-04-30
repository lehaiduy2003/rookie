package com.example.assignment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdatingReq {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
}
