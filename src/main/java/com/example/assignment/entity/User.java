package com.example.assignment.entity;

import com.example.assignment.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntityAudit implements UserDetails {
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    @Column(name = "is_active")
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the role to a list of GrantedAuthority
        // using SimpleGrantedAuthority to represent the role
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if (isActive == null) {
            isActive = true;
        }
    }
}
