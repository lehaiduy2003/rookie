package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.request.RegisterReq;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.response.AuthRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Logging
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final PasswordUtil passwordUtil;

    @Override
    @Transactional
    public AuthRes register(RegisterReq registerReq, HttpServletResponse response) {
        // Create user creation request from register request
        UserCreationReq userCreationReq = UserCreationReq.builder()
                .email(registerReq.getEmail())
                .password(passwordUtil.encode(registerReq.getPassword()))
                .firstName(registerReq.getFirstName())
                .lastName(registerReq.getLastName())
                .phoneNumber(registerReq.getPhoneNumber())
                .role(Role.CUSTOMER) // Default role for registered users
                .build();

        // Create the user
        userService.createUser(userCreationReq);

        // Load user details for token generation
        User user = (User) userService.loadUserByUsername(registerReq.getEmail());

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        // Set refresh token in cookie
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Set authentication in a security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        UserDetailsRes userDetailsRes = userService.getUserById(user.getId());

        // Return authentication response
        return new AuthRes(userDetailsRes, accessToken, refreshToken);
    }

    @Override
    public AuthRes login(LoginReq loginReq, HttpServletResponse response) {
        // Load user from database
        User user = (User) userService.loadUserByUsername(loginReq.getEmail());

        // Check if a user exists
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Verify password
        if (!passwordUtil.matches(loginReq.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Set authentication in a security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
        }

        try {
            // Extract username from a refresh token
            String username = jwtProvider.extractUsername(refreshToken);

            if (username == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }
            // Load user details
            UserDetails userDetails = userService.loadUserByUsername(username);

            // Validate refresh token
            if (!Boolean.TRUE.equals(jwtProvider.validateToken(refreshToken, userDetails))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            // Generate a new access token
            String accessToken = jwtProvider.generateAccessToken(userDetails);

            // Set authentication in a security context
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Return only the access token
            return accessToken;

        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }
}
