package com.example.assignment.entity;

import com.example.assignment.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class User extends BaseEntityAudit {
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    @Column(name = "is_active")
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @Override
    public void prePersist() {
        super.prePersist();
        if (isActive == null) {
            isActive = true;
        }
    }
}
