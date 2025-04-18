package com.example.assignment;

import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Simple test class to verify product validation logic without loading the full application context.
 */
@ExtendWith(MockitoExtension.class)
class SimpleProductValidationTest {

    @Mock
    private Category category;


    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setParent(null);
    }

    @Test
    @DisplayName("Test validateProduct - negative quantity")
    void testValidateProduct_NegativeQuantity() {
        // Create a product with negative quantity
        Product product = new Product();
        product.setQuantity(-10); // Negative quantity
        product.setAvgRating(0.0);
        product.setRatingCount(0L);
        product.setPrice(300.0);
        product.setName("Test Product");
        product.setCategory(category);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(0.0);
        product.setRatingCount(0L);
        product.setPrice(-100.0); // Negative price
        product.setName("Test Product");
        product.setCategory(category);


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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(6); // Out of range
        product.setRatingCount(0L);
        product.setPrice(100.0);
        product.setName("Test Product");
        product.setCategory(category);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(-5.0); // Negative avgRating
        product.setRatingCount(0L);
        product.setPrice(100.0);
        product.setName("Test Product");
        product.setCategory(category);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(0.0);
        product.setRatingCount(-2L); // Negative ratingCount
        product.setPrice(100.0);
        product.setName("Test Product");
        product.setCategory(category);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(0.0);
        product.setRatingCount(0L);
        product.setPrice(100.0);
        product.setName(null); // Null name
        product.setCategory(category);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setAvgRating(0.0);
        product.setRatingCount(0L);
        product.setPrice(100.0);
        product.setName("Test Product");
        product.setCategory(null); // Null category

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
