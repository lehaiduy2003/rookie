package com.example.assignment;

import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.Rating;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * Test class for product permissions and validation logic.
 */
@ExtendWith(MockitoExtension.class)
class ProductPermissionTest {

    @Mock
    private Product product;

    @Mock
    private Category category;

    @Mock
    private User adminUser;

    @Mock
    private User customerUser;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // Setup common mocks for all tests
        when(product.getName()).thenReturn("Test Product");
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getQuantity()).thenReturn(10);
        when(product.getCategory()).thenReturn(category);
    }

    @Test
    @DisplayName("Test admin can update any product")
    void testAdminCanUpdateAnyProduct() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(product.getCreatedBy()).thenReturn(customerUser); // Product created by a customer

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(20)
                .build();

        // Test
        assertDoesNotThrow(() -> productService.updateProductById(1L, updateReq));

        // Verify
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Test customer can update their own product")
    void testCustomerCanUpdateOwnProduct() {
        // Setup
        when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(product.getCreatedBy()).thenReturn(customerUser); // Product created by the same customer

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(20)
                .build();

        // Test
        assertDoesNotThrow(() -> productService.updateProductById(1L, updateReq));

        // Verify
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Test customer cannot update products they didn't create")
    void testCustomerCannotUpdateOtherProducts() {
        // Setup
        when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        User otherUser = mock(User.class);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(product.getCreatedBy()).thenReturn(otherUser); // Product created by another user

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(20)
                .build();

        // Test
        assertThrows(AccessDeniedException.class, () -> productService.updateProductById(1L, updateReq));

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test only admin can update featured attribute")
    void testOnlyAdminCanUpdateFeaturedAttribute() {
        // Setup for admin
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Test admin can update featured
        assertDoesNotThrow(() -> productService.updateToFeaturedProduct(1L, true));

        // Setup for a customer
        when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
        when(authentication.getPrincipal()).thenReturn(customerUser);

        // Test customer cannot update featured
        assertThrows(AccessDeniedException.class, () -> productService.updateToFeaturedProduct(1L, true));
    }

    @Test
    @DisplayName("Test quantity cannot be updated to negative")
    void testQuantityCannotBeUpdatedToNegative() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        // Create a real product instead of a mock for validation testing
        Product realProduct = new Product();
        realProduct.setName("Test Product");
        realProduct.setPrice(100.0);
        realProduct.setQuantity(10);
        realProduct.setCategory(category);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(realProduct));

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(-1) // Negative quantity
                .build();

        // Test
        assertThrows(IllegalArgumentException.class, () -> productService.updateProductById(1L, updateReq));

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test quantity cannot be updated to null")
    void testQuantityCannotBeUpdatedToNull() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(product.getQuantity()).thenReturn(10); // Current quantity

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(null) // Null quantity
                .build();

        // Test
        assertDoesNotThrow(() -> productService.updateProductById(1L, updateReq));

        // Verify that quantity was not updated (since it was null in the request)
        verify(product, never()).setQuantity(anyInt());
    }

    @Test
    @DisplayName("Test product with ratings cannot be deleted")
    void testProductWithRatingsCannotBeDeleted() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Set<Rating> ratings = new HashSet<>();
        ratings.add(mock(Rating.class));
        when(product.getRatings()).thenReturn(ratings);

        // Test
        assertThrows(IllegalStateException.class, () -> productService.deleteProductById(1L));

        // Verify
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Test product with ratings can be archived")
    void testProductWithRatingsCanBeArchived() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Set<Rating> ratings = new HashSet<>();
        ratings.add(mock(Rating.class));
        when(product.getRatings()).thenReturn(ratings);

        // Create a method to archive a product
        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
                .name("Test Product")
                .description("Test Description")
                .price(100.0)
                .quantity(10)
                .isActive(false) // Archive the product
                .build();

        // Test
        assertDoesNotThrow(() -> productService.updateProductById(1L, updateReq));

        // Verify
        verify(product).setIsActive(false);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Test product without ratings can be deleted")
    void testProductWithoutRatingsCanBeDeleted() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        when(product.getRatings()).thenReturn(new HashSet<>());

        // Test
        assertDoesNotThrow(() -> productService.deleteProductById(1L));

        // Verify
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Test category must exist when updating product")
    void testCategoryMustExistWhenUpdatingProduct() {
        // Setup
        when(adminUser.getRole()).thenReturn(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Test
        assertThrows(IllegalArgumentException.class, () -> productService.updateProductCategoryById(1L, 999L));

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }
}
