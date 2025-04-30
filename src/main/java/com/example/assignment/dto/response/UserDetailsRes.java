package com.example.assignment.dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
public class UserDetailsRes extends UserRes {
    private String email;
    private String phoneNumber;
    private String address;
    private String bio;
    private Date dob;
    private Date createdOn;
    private Date updatedOn;

    @Builder
    public UserDetailsRes(Long id, String firstName, String lastName, String avatar, String role, String memberTier, boolean isActive, String email, String phoneNumber, String address, String bio, Date dob, Date createdOn, Date updatedOn) {
        super(id, firstName, lastName, avatar, role, memberTier, isActive);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bio = bio;
        this.dob = dob;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }
}
