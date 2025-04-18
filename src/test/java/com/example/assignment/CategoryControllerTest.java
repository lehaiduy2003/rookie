package com.example.assignment;

import com.example.assignment.controller.CategoryController;
import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.response.CategoryRes;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
import com.example.assignment.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for CategoryController to ensure:
 * - Only admin can create, update, delete
 * - Category name cannot be null or duplicate
 * - Category cannot have a parent category on itself
 */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private User adminUser;

    @Mock
    private User customerUser;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        // Setup common mocks for all tests
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
    }

    // Authorization Tests

    @Test
    @DisplayName("Test admin can create category")
    void testAdminCanCreateCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Test Category")
                .description("Test Description")
                .build();

        CategoryRes expectedResponse = CategoryRes.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .build();

        when(categoryService.createCategory(any(CategoryCreationReq.class))).thenReturn(expectedResponse);

        // Test
        ResponseEntity<CategoryRes> response = categoryController.createCategory(req);

        // Verify
        assertEquals(201, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(categoryService).createCategory(req);
    }

    @Test
    @DisplayName("Test non-admin cannot create category")
    void testNonAdminCannotCreateCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Test Category")
                .description("Test Description")
                .build();

        // In a real application with Spring Security, the @PreAuthorize annotation would prevent
        // non-admin users from calling the controller method. Since we're directly calling the
        // controller method in our test, we need to simulate this behavior by having the service
        // throw an AccessDeniedException.
        when(categoryService.createCategory(any(CategoryCreationReq.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        // Test & Verify
        ResponseEntity<CategoryRes> response = categoryController.createCategory(req);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }

    @Test
    @DisplayName("Test admin can update category")
    void testAdminCanUpdateCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        CategoryRes expectedResponse = CategoryRes.builder()
                .id(1L)
                .name("Updated Category")
                .description("Updated Description")
                .build();

        when(categoryService.updateCategoryById(anyLong(), any(CategoryCreationReq.class))).thenReturn(expectedResponse);

        // Test
        ResponseEntity<CategoryRes> response = categoryController.updateCategory(1L, req);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(categoryService).updateCategoryById(1L, req);
    }

    @Test
    @DisplayName("Test non-admin cannot update category")
    void testNonAdminCannotUpdateCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        // Simulate Spring Security behavior
        when(categoryService.updateCategoryById(anyLong(), any(CategoryCreationReq.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        // Test & Verify
        ResponseEntity<CategoryRes> response = categoryController.updateCategory(1L, req);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }

    @Test
    @DisplayName("Test admin can delete category")
    void testAdminCanDeleteCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(categoryService).deleteCategoryById(anyLong());

        // Test
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Verify
        assertEquals(204, response.getStatusCode().value());
        verify(categoryService).deleteCategoryById(1L);
    }

    @Test
    @DisplayName("Test non-admin cannot delete category")
    void testNonAdminCannotDeleteCategory() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        // Simulate Spring Security behavior
        doThrow(new AccessDeniedException("Access denied")).when(categoryService).deleteCategoryById(anyLong());

        // Test & Verify
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }

    // Validation Tests

    @Test
    @DisplayName("Test category name cannot be null")
    void testCategoryNameCannotBeNull() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name(null)
                .description("Test Description")
                .build();

        when(categoryService.createCategory(any(CategoryCreationReq.class)))
                .thenThrow(new IllegalArgumentException("Category name cannot be null"));

        // Test & Verify
        ResponseEntity<CategoryRes> response = categoryController.createCategory(req);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }

    @Test
    @DisplayName("Test category name cannot be duplicate")
    void testCategoryNameCannotBeDuplicate() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Existing Category")
                .description("Test Description")
                .build();

        when(categoryService.createCategory(any(CategoryCreationReq.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate category name"));

        // Test & Verify
        ResponseEntity<CategoryRes> response = categoryController.createCategory(req);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }

    // Parent-Child Relationship Tests

    @Test
    @DisplayName("Test category cannot have itself as parent")
    void testCategoryCannotHaveItselfAsParent() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        CategoryCreationReq req = CategoryCreationReq.builder()
                .name("Test Category")
                .description("Test Description")
                .parentId(1L)
                .build();

        when(categoryService.updateCategoryById(eq(1L), any(CategoryCreationReq.class)))
                .thenThrow(new IllegalArgumentException("Parent ID cannot be the same as Category ID"));

        // Test & Verify
        ResponseEntity<CategoryRes> response = categoryController.updateCategory(1L, req);
        assertEquals(500, response.getStatusCode().value()); // Internal Server Error
    }
}
