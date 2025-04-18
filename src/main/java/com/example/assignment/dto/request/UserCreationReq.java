package com.example.assignment.dto.request;

import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserCreationReq {
    @Email
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Role role;
    private String avatar;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dob;
    private String bio;
    private MemberTier memberTier;
}
