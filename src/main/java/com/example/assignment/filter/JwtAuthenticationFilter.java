package com.example.assignment.filter;

import com.example.assignment.entity.User;
import com.example.assignment.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for JWT authentication.
 * Intercepts requests and validates JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check if the Authorization header is present and starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Extract JWT token from the Authorization header
        jwt = authHeader.substring(7);
        // Check if the JWT token is valid
        if (jwt.isEmpty() || !jwtProvider.validateToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // start setting authentication
        // Extract username from JWT token
        userEmail = jwtProvider.extractUsername(jwt);

        // Check if the username is not null and authentication is not yet set
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from database
            User user = (User) this.userDetailsService.loadUserByUsername(userEmail);

            // check if this token's owner is the same as the one in the database
            if(!userEmail.equals(user.getEmail())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Create an authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
            );

            // Set details for the authentication token
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}