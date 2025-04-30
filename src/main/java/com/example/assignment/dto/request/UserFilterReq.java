package com.example.assignment.dto.request;

import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserFilterReq {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private MemberTier memberTier;
    private Boolean isActive;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date updatedOn;
}
