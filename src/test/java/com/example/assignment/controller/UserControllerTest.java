package com.example.assignment.controller;

import com.example.assignment.AssignmentApplication;
import com.example.assignment.annotation.WithMockCustomUser;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.example.assignment.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = AssignmentApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test non-admin cannot create user")
    @WithMockUser(roles = "CUSTOMER") // Mock user with a different role
    void testNonAdminCannotCreateUser() throws Exception {
        // Mock the service call
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(getUserCreationReq())))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin have permission to create user")
    @WithMockUser(roles = "ADMIN") // Mock user with a different admin role
    void testAdminCanCreateUser() throws Exception {
        // Mock the service call
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(getUserCreationReq())))
            .andExpect(status().isCreated());
    }

    @WithMockCustomUser(id = 99L, username = "admin", roles = {"ADMIN"})
    @Test
    void testAdminCanUpdateAnyUser() throws Exception {
        Long userId = 1L;
        UserInfoUpdatingReq req = getUserInfoUpdatingReq();

        UserDetailsRes expectedRes = getUserDetailsRes();

        Mockito.when(userService.updateUserById(eq(userId), any(UserInfoUpdatingReq.class)))
            .thenReturn(expectedRes);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("User"));
    }

    private static UserCreationReq getUserCreationReq() {
        return UserCreationReq.builder()
            .firstName("John")
            .lastName("Doe")
            .role(Role.CUSTOMER)
            .memberTier(MemberTier.COMMON)
            .email("test@example.com")
            .password("password")
            .phoneNumber("1234567890")
            .address("123 Test St")
            .bio("Test bio")
            .build();
    }

    private static UserInfoUpdatingReq getUserInfoUpdatingReq() {
        return UserInfoUpdatingReq.builder()
            .firstName("Updated")
            .lastName("User")
            .address("123 Test St")
            .bio("Test bio")
            .avatar("updated.png")
            .build();

    }

    private static UserDetailsRes getUserDetailsRes() {
        return UserDetailsRes.builder()
            .id(1L)
            .firstName("Updated")
            .lastName("User")
            .address("123 Test St")
            .bio("Test bio")
            .avatar("updated.png")
            .role("CUSTOMER")
            .memberTier("COMMON")
            .updatedOn(new Date())
            .build();
    }

    @WithMockCustomUser(id = 1L, username = "user1", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Test customer can update their own information")
    void testCustomerCanUpdateUser() throws Exception {

        Long userId = 1L;
        UserInfoUpdatingReq req = getUserInfoUpdatingReq();

        UserDetailsRes expectedResponse = getUserDetailsRes();

        // Mock the service call
        Mockito.when(userService.updateUserById(eq(userId), any(UserInfoUpdatingReq.class)))
            .thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("User"));
    }

    @WithMockCustomUser(id = 2L, username = "user2", roles = {"CUSTOMER"})
    @DisplayName("Test customer can not update other user information")
    @Test
    void customerCannotUpdateOtherUser() throws Exception {
        Long userId = 1L; // different customer id
        UserInfoUpdatingReq req = UserInfoUpdatingReq.builder()
            .firstName("Hack")
            .lastName("Attempt")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test admin can delete user")
    @WithMockUser(roles = "ADMIN")
    void testAdminCanDeleteUser() throws Exception {
        Long userId = 2L;

        Mockito.doNothing().when(userService).deleteUserById(userId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{id}", userId))
            .andExpect(status().isNoContent()); // 204

        // verify that the method invocation succeed
        Mockito.verify(userService, Mockito.times(1)).deleteUserById(userId);

    }

    @Test
    @DisplayName("non-admin cannot delete user - Forbidden")
    @WithMockUser()
    void testNonAdminCannotDeleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/users/{id}", userId))
            .andExpect(status().isForbidden());

        Mockito.verify(userService, Mockito.never()).deleteUserById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin tries to delete non-existent user - Not Found")
    void testAdminDeleteNonExistentUser() throws Exception {
        Long userId = 999L;

        Mockito.doThrow(new UsernameNotFoundException("User not found")).when(userService).deleteUserById(userId);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/users/{id}", userId))
            .andExpect(status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).deleteUserById(userId);
    }
}
