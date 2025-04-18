package com.example.assignment.config;

import com.example.assignment.annotation.WithMockCustomUser;
import com.example.assignment.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory to create a SecurityContext with a mock user for testing.
 * This class is used in conjunction with the @WithMockCustomUser annotation.
 * It sets up a SecurityContext with a mock user and their roles.
 * The mock user is created based on the parameters provided in the annotation.
 */
public class WithMockCustomerUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> authorities = Arrays.stream(customUser.roles())
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());

        User principal = new User();
        principal.setId(customUser.id());
        principal.setEmail(customUser.username());

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", authorities);
        context.setAuthentication(auth);
        return context;
    }
}
