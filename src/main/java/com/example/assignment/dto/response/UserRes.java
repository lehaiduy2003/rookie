package com.example.assignment.dto.response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRes {
    private Long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String role;
    private String memberTier;
}
