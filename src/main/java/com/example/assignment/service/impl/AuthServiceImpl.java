package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.request.RegisterReq;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.response.AuthRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
import com.example.assignment.exception.ExistingResourceException;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.exception.UnAuthorizedException;
import com.example.assignment.service.AuthService;
import com.example.assignment.service.UserService;
import com.example.assignment.util.CookieUtil;
import com.example.assignment.provider.JwtProvider;
import com.example.assignment.util.PasswordUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final PasswordUtil passwordUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    @Override
    @Transactional
    public AuthRes register(RegisterReq registerReq, HttpServletRequest request, HttpServletResponse response) {
        // Create user creation request from register request
        // Check if the email already exists
        if (userService.existsByEmail(registerReq.getEmail())) {
            throw new ExistingResourceException("User with this email already exists");
        }
        // Create a new user creation request
        UserCreationReq userCreationReq = UserCreationReq.builder()
                .email(registerReq.getEmail())
                .password(registerReq.getPassword()) // the password is already hashed in the UserService
                .firstName(registerReq.getFirstName())
                .lastName(registerReq.getLastName())
                .phoneNumber(registerReq.getPhoneNumber())
                .role(Role.CUSTOMER) // Default role for registered users
                .build();

        // Create the user
        userService.createUser(userCreationReq);

        // Load user details for token generation
        User user = (User) loadUserByUsername(registerReq.getEmail());

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        // Set refresh token in cookie
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Set authentication in a security context
        setAuth(user, request);

        // Get user details
        UserDetailsRes userDetailsRes = userService.getUserById(user.getId());

        // Return authentication response
        return new AuthRes(userDetailsRes, accessToken, refreshToken);
    }

    @Override
    public AuthRes login(LoginReq loginReq, HttpServletRequest request, HttpServletResponse response) {
        // Load user from database
        User user = (User) loadUserByUsername(loginReq.getEmail());

        // Check if a user exists
        if (user == null) {
            throw new ResourceNotFoundException("User's email not found");
        }

        // Verify password
        if (!passwordUtil.matches(loginReq.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException("Invalid password");
        }

        // Set authentication in a security context
        setAuth(user, request);

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        // Set refresh token in cookie
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Get user details
        UserDetailsRes userDetailsRes = userService.getUserById(user.getId());

        // Return authentication response
        return new AuthRes(userDetailsRes, accessToken, refreshToken);
    }

    @Override
    public void logout(HttpServletResponse response) {
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Clear refresh token cookie
        cookieUtil.clearRefreshTokenCookie(response);
    }

    @Override
    public String refreshToken(HttpServletRequest request) {
        // Extract refresh token from cookies
        String refreshToken = cookieUtil.extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new UnAuthorizedException("Refresh token not found");
        }

        try {
            // Extract username from a refresh token
            String username = jwtProvider.extractUsername(refreshToken);

            if (username == null) {
                throw new UnAuthorizedException("Invalid refresh token");
            }
            // Load user details
            User user = (User) loadUserByUsername(username);

            // Validate refresh token
            if (!Boolean.TRUE.equals(jwtProvider.validateToken(refreshToken))) {
                throw new UnAuthorizedException("Invalid refresh token");
            }

            // Generate a new access token
            String accessToken = jwtProvider.generateAccessToken(user);

            // Set authentication in a security context
            setAuth(user, request);

            // Return only the access token
            return accessToken;

        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException("Refresh token expired");
        } catch (JwtException e) {
            throw new UnAuthorizedException("Invalid refresh token");
        }
    }

    private void setAuth(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }
}
