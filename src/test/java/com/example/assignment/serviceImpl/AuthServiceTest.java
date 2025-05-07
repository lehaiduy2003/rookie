package com.example.assignment.serviceImpl;

import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.request.RegisterReq;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.response.AuthRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.enums.Role;
import com.example.assignment.exception.ExistingResourceException;
import com.example.assignment.exception.UnAuthorizedException;
import com.example.assignment.provider.JwtProvider;
import com.example.assignment.service.UserService;
import com.example.assignment.service.impl.AuthServiceImpl;
import com.example.assignment.util.CookieUtil;
import com.example.assignment.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Test successful user registration")
    void testRegisterSuccess() {
        // Setup
        RegisterReq registerReq = new RegisterReq();
        registerReq.setEmail("test@example.com");
        registerReq.setPassword("password123");
        registerReq.setFirstName("John");
        registerReq.setLastName("Doe");
        registerReq.setPhoneNumber("1234567890");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create user details response
        UserDetailsRes userDetailsRes = new UserDetailsRes(
            1L, "John", "Doe", null, "CUSTOMER", null, true,
            "test@example.com", "1234567890", null, null, null, null, null
        );

        // Mock user returned after creation
        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setEmail(registerReq.getEmail());
        createdUser.setRole(Role.CUSTOMER);

        // Expected tokens
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        AuthRes authRes = new AuthRes();
        authRes.setAccessToken(accessToken);
        authRes.setRefreshToken(refreshToken);
        authRes.setUserDetails(userDetailsRes);

        // Setup mocks with argument captor
        ArgumentCaptor<UserCreationReq> userCreationCaptor = ArgumentCaptor.forClass(UserCreationReq.class);
        when(userService.existsByEmail(registerReq.getEmail())).thenReturn(false);
        when(userService.createUser(userCreationCaptor.capture())).thenReturn(userDetailsRes);
        when(userService.findByEmail(registerReq.getEmail())).thenReturn(createdUser);
        when(jwtProvider.generateAccessToken(createdUser)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(createdUser)).thenReturn(refreshToken);

        // Execute
        AuthRes result = authService.register(registerReq, request, response);

        // Verify
        assertNotNull(result);
        assertEquals(authRes, result);

        // Verify correct request mapping
        UserCreationReq capturedReq = userCreationCaptor.getValue();
        assertEquals(registerReq.getEmail(), capturedReq.getEmail());
        assertEquals(registerReq.getPassword(), capturedReq.getPassword());
        assertEquals(registerReq.getFirstName(), capturedReq.getFirstName());
        assertEquals(registerReq.getLastName(), capturedReq.getLastName());
        assertEquals(registerReq.getPhoneNumber(), capturedReq.getPhoneNumber());
        assertEquals(Role.CUSTOMER, capturedReq.getRole());

        // Verify cookie setting
        verify(cookieUtil).addRefreshTokenCookie(response, refreshToken);
    }

    @Test
    @DisplayName("Test registration with existing email")
    void testRegisterWithExistingEmail() {
        // Setup
        RegisterReq registerReq = new RegisterReq();
        registerReq.setEmail("existing@example.com");
        registerReq.setPassword("password123");
        registerReq.setFirstName("John");
        registerReq.setLastName("Doe");
        registerReq.setPhoneNumber("1234567890");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock behavior - email exists
        when(userService.existsByEmail(registerReq.getEmail())).thenReturn(true);

        // Verify exception is thrown
        assertThrows(ExistingResourceException.class, () -> {
            authService.register(registerReq, request, response);
        });

        // Verify no other interactions happened
        verify(userService, never()).createUser(any());
        verify(userService, never()).findByEmail(any());
        verify(jwtProvider, never()).generateAccessToken(any());
        verify(jwtProvider, never()).generateRefreshToken(any());
        verify(cookieUtil, never()).addRefreshTokenCookie(any(), any());
    }

    @Test
    @DisplayName("Test login with valid credentials")
    void testLoginSuccess() {
        // Setup
        LoginReq loginReq = new LoginReq();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("password123");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock user details
        UserDetailsRes userDetailsRes = UserDetailsRes.builder()
            .id(1L)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .role("CUSTOMER")
            .phoneNumber("1234567890")
            .address("123 Main St")
            .isActive(true)
            .build();

        // Mock user
        User user = new User();
        user.setId(1L);
        user.setEmail(loginReq.getEmail());
        user.setPassword("encoded-password");
        user.setRole(Role.CUSTOMER);
        user.setIsActive(true);

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setPhoneNumber("1234567890");
        userProfile.setAddress("123 Main St");

        user.setUserProfile(userProfile);

        // Expected tokens
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        // Setup mocks
        when(userService.findByEmail(loginReq.getEmail())).thenReturn(user);
        when(passwordUtil.matches(loginReq.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(user)).thenReturn(refreshToken);

        // Execute
        AuthRes result = authService.login(loginReq, request, response);

        // Verify
        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        assertEquals(userDetailsRes.getEmail(), result.getUserDetails().getEmail());
        assertEquals(userDetailsRes.getPhoneNumber(), result.getUserDetails().getPhoneNumber());

        // Verify cookie was set
        verify(cookieUtil).addRefreshTokenCookie(response, refreshToken);
    }

    @Test
    @DisplayName("Test login with invalid password")
    void testLoginWithInvalidPassword() {
        // Setup
        LoginReq loginReq = new LoginReq();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("wrong-password");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock user
        User user = new User();
        user.setId(1L);
        user.setEmail(loginReq.getEmail());
        user.setPassword("encoded-password");

        // Setup mocks
        when(userService.findByEmail(loginReq.getEmail())).thenReturn(user);
        when(passwordUtil.matches(loginReq.getPassword(), user.getPassword())).thenReturn(false);

        // Execute and verify
        assertThrows(UnAuthorizedException.class, () ->
            authService.login(loginReq, request, response));

        // Verify no tokens were generated and no cookie was set
        verify(jwtProvider, never()).generateAccessToken(any());
        verify(jwtProvider, never()).generateRefreshToken(any());
        verify(cookieUtil, never()).addRefreshTokenCookie(any(), any());
    }

    @Test
    @DisplayName("Test login with non-existent email")
    void testLoginWithNonExistentEmail() {
        // Setup
        LoginReq loginReq = new LoginReq();
        loginReq.setEmail("nonexistent@example.com");
        loginReq.setPassword("password123");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock user not found
        when(userService.findByEmail(loginReq.getEmail())).thenReturn(null);
        // Execute and verify
        assertThrows(UsernameNotFoundException.class, () ->
            authService.login(loginReq, request, response));

        // Verify no password checking or token generation happened
        verify(passwordUtil, never()).matches(any(), any());
        verify(jwtProvider, never()).generateAccessToken(any());
        verify(cookieUtil, never()).addRefreshTokenCookie(any(), any());
    }

    @Test
    @DisplayName("Test logout")
    void testLogout() {
        // Setup
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Execute
        authService.logout(response);

        // Verify
        verify(cookieUtil).clearRefreshTokenCookie(response);
    }

    @Test
    @DisplayName("Test refresh token with valid token")
    void testRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String refreshToken = "test-refresh-token";
        String newAccessToken = "new-access-token";

        User user = new User();
        user.setId(1L);
        user.setEmail("email@example.com");
        user.setRole(Role.CUSTOMER);

        // Mock refresh token
        when(cookieUtil.extractRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtProvider.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.generateAccessToken(any())).thenReturn(newAccessToken);

        // Execute
        String result = authService.refreshToken(request);

        // Verify
        assertEquals(newAccessToken, result);

    }

    @Test
    @DisplayName("Test refresh token with missing token")
    void testRefreshTokenWithMissingToken() {
        // setup mock
        HttpServletRequest request = mock(HttpServletRequest.class);

        // stub the behavior
        when(cookieUtil.extractRefreshTokenFromCookie(request)).thenReturn(null);

        // execute and verify
        assertThrows(UnAuthorizedException.class, () -> {
            authService.refreshToken(request);
        });

        // verify no token was generated
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Test refresh token with invalid token")
    void testRefreshTokenWithInvalidToken() {
        // setup mock
        HttpServletRequest request = mock(HttpServletRequest.class);

        String refreshToken = "invalid-refresh-token";

        User user = new User();
        user.setId(1L);
        user.setEmail("email@example.com");
        user.setRole(Role.CUSTOMER);

        // stub the behavior
        when(cookieUtil.extractRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtProvider.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

        // execute and verify
        assertThrows(UnAuthorizedException.class, () -> {
            authService.refreshToken(request);
        });

        // verify no token was generated
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Test refresh token with non-user email in payload")
    void testRefreshTokenWithInvalidPayload() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "invalid-refresh-token";

        when(cookieUtil.extractRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtProvider.extractUsername(refreshToken)).thenReturn(null);

        // execute and verify
        assertThrows(UnAuthorizedException.class, () -> {
            authService.refreshToken(request);
        });

        // verify no token was generated
        verify(jwtProvider, never()).generateAccessToken(any());
    }
}
