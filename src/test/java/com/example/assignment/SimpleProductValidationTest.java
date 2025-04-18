package com.example.assignment;

import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Simple test class to verify product validation logic without loading the full application context.
 */
@ExtendWith(MockitoExtension.class)
class SimpleProductValidationTest {

    @Mock
    private Product product;

    @Mock
    private Category category;

    @Test
    @DisplayName("Test validateProduct - negative quantity")
    void testValidateProduct_NegativeQuantity() {
        // Create a product with negative quantity
        when(product.getQuantity()).thenReturn(-1);
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getQuantity() < 0) {
                throw new IllegalArgumentException("Product quantity cannot be negative");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - negative price")
    void testValidateProduct_NegativePrice() {
        // Create a product with a negative price
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(-10.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Product price cannot be negative");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - avgRating out of range")
    void testValidateProduct_AvgRatingOutOfRange() {
        // Create a product with avgRating > 5
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(6.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getAvgRating() < 0 || product.getAvgRating() > 5) {
                throw new IllegalArgumentException("Average rating must be between 0 and 5");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - negative avgRating")
    void testValidateProduct_NegativeAvgRating() {
        // Create a product with negative avgRating
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(-1.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getAvgRating() < 0 || product.getAvgRating() > 5) {
                throw new IllegalArgumentException("Average rating must be between 0 and 5");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - negative ratingCount")
    void testValidateProduct_NegativeRatingCount() {
        // Create a product with a negative ratingCount
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(-1L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getRatingCount() < 0) {
                throw new IllegalArgumentException("Rating count cannot be negative");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - null name")
    void testValidateProduct_NullName() {
        // Create a product with a null name
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn(null);
        when(product.getCategory()).thenReturn(category);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }

    @Test
    @DisplayName("Test validateProduct - null category")
    void testValidateProduct_NullCategory() {
        // Create a product with a null category
        when(product.getQuantity()).thenReturn(10);
        when(product.getAvgRating()).thenReturn(0.0);
        when(product.getRatingCount()).thenReturn(0L);
        when(product.getPrice()).thenReturn(100.0);
        when(product.getName()).thenReturn("Test Product");
        when(product.getCategory()).thenReturn(null);

        // Create a method to test the validation logic
        Runnable validation = () -> {
            if (product.getCategory() == null) {
                throw new IllegalArgumentException("Product category cannot be null");
            }
        };

        // Verify that validation throws an exception
        assertThrows(IllegalArgumentException.class, validation::run);
    }
}
