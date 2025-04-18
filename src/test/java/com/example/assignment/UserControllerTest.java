package com.example.assignment;

import com.example.assignment.controller.UserController;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.User;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.example.assignment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Test class for UserController to ensure:
 * - Only admin can create and delete users
 * - Both admin and customer can update user information
 * - User retrieval works correctly
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private User adminUser;

    @Mock
    private User customerUser;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Setup common mocks for all tests
        lenient().when(adminUser.getRole()).thenReturn(Role.ADMIN);
        lenient().when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
    }

    // Authorization Tests

    @Test
    @DisplayName("Test admin can create user")
    void testAdminCanCreateUser() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        UserCreationReq req = UserCreationReq.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .memberTier(MemberTier.COMMON)
                .build();

        UserRes expectedResponse = UserRes.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .build();

        when(userService.createUser(any(UserCreationReq.class))).thenReturn(expectedResponse);

        // Test
        ResponseEntity<UserRes> response = userController.createUser(req);

        // Verify
        assertEquals(201, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).createUser(req);
    }

    @Test
    @DisplayName("Test non-admin cannot create user")
    void testNonAdminCannotCreateUser() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        UserCreationReq req = UserCreationReq.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .memberTier(MemberTier.COMMON)
                .build();

        // Simulate Spring Security behavior
        when(userService.createUser(any(UserCreationReq.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        // Test & Verify
        ResponseEntity<UserRes> response = userController.createUser(req);
        assertEquals(403, response.getStatusCode().value()); // Forbidden
    }

    @Test
    @DisplayName("Test admin can update user")
    void testAdminCanUpdateUser() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        UserInfoUpdatingReq req = UserInfoUpdatingReq.builder()
                .firstName("Updated")
                .lastName("User")
                .address("123 Test St")
                .bio("Test bio")
                .build();

        UserRes expectedResponse = UserRes.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .build();

        when(userService.updateUserById(anyLong(), any(UserInfoUpdatingReq.class))).thenReturn(expectedResponse);

        // Test
        ResponseEntity<UserRes> response = userController.updateUser(1L, req);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).updateUserById(1L, req);
    }

    @Test
    @DisplayName("Test customer can update own information")
    void testCustomerCanUpdateOwnInformation() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        UserInfoUpdatingReq req = UserInfoUpdatingReq.builder()
                .firstName("Updated")
                .lastName("User")
                .address("123 Test St")
                .bio("Test bio")
                .build();

        UserRes expectedResponse = UserRes.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .build();

        when(userService.updateUserById(anyLong(), any(UserInfoUpdatingReq.class))).thenReturn(expectedResponse);

        // Test
        ResponseEntity<UserRes> response = userController.updateUser(1L, req);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).updateUserById(1L, req);
    }

    @Test
    @DisplayName("Test admin can delete user")
    void testAdminCanDeleteUser() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(userService).deleteUserById(anyLong());

        // Test
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Verify
        assertEquals(204, response.getStatusCode().value());
        verify(userService).deleteUserById(1L);
    }

    @Test
    @DisplayName("Test non-admin cannot delete user")
    void testNonAdminCannotDeleteUser() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        // Simulate Spring Security behavior
        doThrow(new AccessDeniedException("Access denied")).when(userService).deleteUserById(anyLong());

        // Test & Verify
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(403, response.getStatusCode().value()); // Forbidden
    }

    // Retrieval Tests

    @Test
    @DisplayName("Test get user by id")
    void testGetUserById() {
        // Setup
        UserDetailsRes expectedResponse = UserDetailsRes.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .isActive(true)
                .build();

        when(userService.getUserById(anyLong())).thenReturn(expectedResponse);

        // Test
        ResponseEntity<UserDetailsRes> response = userController.getUserById(1L);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Test get pageable users")
    void testGetPageableUsers() {
        // Setup
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        List<UserRes> userList = new ArrayList<>();
        userList.add(UserRes.builder().id(1L).firstName("User1").lastName("Test").role("CUSTOMER").build());
        userList.add(UserRes.builder().id(2L).firstName("User2").lastName("Test").role("CUSTOMER").build());

        PagingRes<UserRes> expectedResponse = PagingRes.<UserRes>builder()
                .content(userList)
                .totalElements(2)
                .totalPages(1)
                .size(10)
                .page(0)
                .empty(false)
                .build();

        when(userService.getUsers(anyInt(), anyInt(), anyString(), anyString())).thenReturn(expectedResponse);

        // Test
        ResponseEntity<PagingRes<UserRes>> response = userController.getPageableUsers(0, 10, "id", "asc");

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).getUsers(0, 10, "asc", "id");
    }
}
