package com.example.assignment;

import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.request.RatingUpdatingReq;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.Rating;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.RatingMapper;
import com.example.assignment.repository.RatingRepository;
import com.example.assignment.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper; // Add this mock

    private Product product;
    private Customer customer;
    private Rating rating;

    @BeforeEach
    void setUp() {

        product = new Product();
        product.setId(1L);

        customer = new Customer();
        customer.setId(1L);

        rating = new Rating();
        rating.setId(1L);
        rating.setScore(4.0);
        rating.setComment("Great product!");
        rating.setProduct(product);
        rating.setCustomer(customer);
    }

    @Test
    @DisplayName("Test rating creation with valid data (must be not negative and within range 1-5)")
    void testScoreCannotBeNegativeOrOutOfRange() {
        RatingCreationReq invalidRatingReq = RatingCreationReq.builder()
                .score(6.0) // Invalid score
                .productId(1L)
                .customerId(1L)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.createRating(invalidRatingReq);
        });

        assertEquals("Score must be between 1 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("Test rating only updated with comment")
    void testOnlyCommentGetsUpdated() {
        RatingUpdatingReq updatingReq = RatingUpdatingReq.builder()
                .comment("Updated comment")
                .build();

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        updatingReq.setCustomerId(1L);
        updatingReq.setComment("Updated comment");

        // Mock the mapper behavior
        when(ratingMapper.toDto(any(Rating.class))).thenReturn(
                RatingRes.builder()
                        .id(1L)
                        .comment("Updated comment")
                        .score(4.0) // Score remains unchanged
                        .build()
        );

        Rating updatedRating = new Rating();
        updatedRating.setId(1L);
        updatedRating.setComment("Updated comment");
        updatedRating.setScore(4.0); // Score remains unchanged
        updatedRating.setProduct(product);
        updatedRating.setCustomer(customer);

        when(ratingRepository.save(any(Rating.class))).thenReturn(updatedRating);

        RatingRes result = ratingService.updateRating(1L, updatingReq);

        assertEquals("Updated comment", result.getComment());
        assertEquals(4.0, result.getScore());
    }

    @Test
    @DisplayName("Test rating updated by owner")
    void testCustomersCanOnlyUpdateTheirOwnRatings() {
        RatingUpdatingReq updatingReq = RatingUpdatingReq.builder()
                .comment("Updated comment")
                .customerId(2L) // Different customer ID
                .build();

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.updateRating(1L, updatingReq);
        });

        assertEquals("Rating can only be updated by the same customer who created it", exception.getMessage());
    }

    @Test
    @DisplayName("Test rating deletion by owner")
    void testCustomersCanOnlyDeleteTheirOwnRatings() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.deleteRating(2L);
        });

        assertEquals("Rating not found with id: 2", exception.getMessage());
    }
}
