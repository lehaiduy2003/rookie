package com.example.assignment.dto.response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {
    private Long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String role;
    private String memberTier;
    private Boolean isActive;
}
