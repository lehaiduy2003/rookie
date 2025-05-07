package com.example.assignment.controller;

import com.example.assignment.AssignmentApplicationTests;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest extends AssignmentApplicationTests {

    @Test
    @DisplayName("Get user details with valid credentials")
    void getUserDetails_Success() {

        ResponseEntity<UserDetailsRes> response = restTemplate.exchange(
            getUrl("/api/v1/users/1"),
            HttpMethod.GET,
            null,
            UserDetailsRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Create new user as admin")
    void createUser_AsAdmin_Success() {
        // Login as admin
        String adminToken = authenticateAndGetToken("admin@example.com", "admin123");

        // Create new user
        UserCreationReq newUser = UserCreationReq.builder()
            .firstName("Test")
            .lastName("User")
            .email("newuser@example.com")
            .password("password123")
            .role(Role.CUSTOMER)
            .memberTier(MemberTier.COMMON)
            .phoneNumber("1234567890")
            .address("Test Address")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<UserDetailsRes> response = restTemplate.exchange(
            getUrl("/api/v1/users"),
            HttpMethod.POST,
            new HttpEntity<>(newUser, headers),
            UserDetailsRes.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test", response.getBody().getFirstName());
        assertEquals("User", response.getBody().getLastName());
        assertEquals("CUSTOMER", response.getBody().getRole());
    }

    @Test
    @DisplayName("Update own profile as customer")
    void updateOwnProfile_AsCustomer_Success() {
        // Login as customer
        String customerToken = authenticateAndGetToken("customer@example.com", "customer123");

        // Update profile
        UserInfoUpdatingReq updateReq = UserInfoUpdatingReq.builder()
            .firstName("Updated")
            .lastName("Customer")
            .address("New Address")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<UserDetailsRes> response = restTemplate.exchange(
            getUrl("/api/v1/users/2"),
            HttpMethod.PUT,
            new HttpEntity<>(updateReq, headers),
            UserDetailsRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getFirstName());
        assertEquals("Customer", response.getBody().getLastName());
        assertEquals("New Address", response.getBody().getAddress());
    }

    @Test
    @DisplayName("Customer cannot update another user's profile")
    void updateOtherProfile_AsCustomer_Forbidden() {
        // Login as customer
        String customerToken = authenticateAndGetToken("customer@example.com", "customer123");

        // Try to update admin profile
        UserInfoUpdatingReq updateReq = UserInfoUpdatingReq.builder()
            .firstName("Hacked")
            .lastName("Admin")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            getUrl("/api/v1/users/1"),
            HttpMethod.PUT,
            new HttpEntity<>(updateReq, headers),
            String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}