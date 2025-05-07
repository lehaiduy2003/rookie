package com.example.assignment.serviceImpl;

import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.Rating;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.RatingMapper;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.repository.RatingRepository;
import com.example.assignment.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RatingServiceImpl ratingServiceImpl;

    static Stream<Arguments> sortingTestCases() {
        return Stream.of(
            Arguments.of("Test with null sort direction", null, "id"),
            Arguments.of("Test with empty sort direction", "", "id"),
            Arguments.of("Test with null sort by", "asc", null),
            Arguments.of("Test with empty sort by", "asc", "")
        );
    }

    @Test
    @DisplayName("Test createRating with valid input")
    void testCreateRating() {
        // Create a valid RatingCreationReq object
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(1L)
            .customerId(1L)
            .score(4.5)
            .comment("Great product!")
            .build();

        UserRes userRes = new UserRes();
        userRes.setId(1L);

        RatingRes ratingRes = RatingRes.builder()
            .productId(1L)
            .customer(userRes)
            .score(4.5)
            .comment("Great product!")
            .build();

        Rating rating = new Rating();
        rating.setScore(4.5);
        rating.setComment("Great product!");

        Product product = new Product();
        product.setId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock the behavior of the repositories and mapper
        when(productRepository.findById(1L)).thenReturn(Optional.of((product)));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(ratingMapper.toEntity(ratingCreationReq)).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingMapper.toDto(any(Rating.class))).thenReturn(ratingRes);

        // Call the createRating method
        RatingRes result = ratingServiceImpl.createRating(ratingCreationReq);

        // Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals(1L, result.getCustomer().getId());
        assertEquals(4.5, result.getScore());
    }

    @Test
    @DisplayName("Test createRating with score higher than 5")
    void testCreateRatingWithScoreHigherThan5() {
        // Create a RatingCreationReq object with an invalid score
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(1L)
            .customerId(1L)
            .score(6.0) // Invalid score
            .comment("Great product!")
            .build();

        // Call the createRating method and expect an exception
        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (IllegalArgumentException e) {
            assertEquals("Score must be between 1 and 5", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createRating with score lower than 1")
    void testCreateRatingWithScoreLowerThan1() {
        // Create a RatingCreationReq object with an invalid score
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(1L)
            .customerId(1L)
            .score(0.5) // Invalid score
            .comment("Great product!")
            .build();

        // Call the createRating method and expect an exception
        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (IllegalArgumentException e) {
            assertEquals("Score must be between 1 and 5", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createRating with null productId")
    void testCreateRatingWithNullProductId() {
        // Create a RatingCreationReq object with a null productId
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(null) // Null productId
            .customerId(1L)
            .score(4.5)
            .comment("Great product!")
            .build();

        // Call the createRating method and expect an exception
        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (IllegalArgumentException e) {
            assertEquals("Product ID and Customer ID cannot be null", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createRating with null customerId")
    void testCreateRatingWithNullCustomerId() {
        // Create a RatingCreationReq object with a null customerId
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(1L)
            .customerId(null) // Null customerId
            .score(4.5)
            .comment("Great product!")
            .build();

        // Call the createRating method and expect an exception
        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (IllegalArgumentException e) {
            assertEquals("Product ID and Customer ID cannot be null", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createRating with non-existent productId")
    void testCreateRatingWithNonExistProduct() {
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(999L)
            .customerId(1L)
            .score(4.5)
            .comment("Great product!")
            .build();

        // Mock the behavior of the repositories
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (ResourceNotFoundException e) {
            assertEquals("Product not found with id: 999", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createRating with non-existent customerId")
    void testCreateRatingWithNonExistCustomer() {
        RatingCreationReq ratingCreationReq = RatingCreationReq.builder()
            .productId(1L)
            .customerId(999L)
            .score(4.5)
            .comment("Great product!")
            .build();

        Product product = new Product();
        product.setId(1L);

        // Mock the behavior of the repositories
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            ratingServiceImpl.createRating(ratingCreationReq);
        } catch (ResourceNotFoundException e) {
            assertEquals("Customer not found with id: 999", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test getRatingById with valid ID")
    void testGetRatingById() {
        // Create a valid ID
        Long ratingId = 1L;

        // Create a mock Rating object
        Rating rating = new Rating();
        rating.setId(ratingId);
        rating.setScore(4.5);
        rating.setComment("Great product!");

        RatingRes ratingRes = RatingRes.builder()
            .productId(1L)
            .customer(new UserRes())
            .score(4.5)
            .comment("Great product!")
            .build();

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingRes);

        // Call the getRatingById method
        RatingRes result = ratingServiceImpl.getRatingById(ratingId);

        // Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
    }

    @Test
    @DisplayName("Test getRatingById with non-existent ID")
    void testGetRatingByNonExistId() {
        Long ratingId = 999L;
        // Mock the behavior of the repository
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());
        // Call the getRatingById method and expect an exception
        try {
            ratingServiceImpl.getRatingById(ratingId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Rating not found with id: 999", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test getAllRatings with valid input")
    void testGetAllRatings() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setScore(4.5);
        rating.setComment("Great product!");

        RatingRes ratingRes = RatingRes.builder()
            .id(1L)
            .score(4.5)
            .comment("Great product!")
            .build();

        List<Rating> ratingList = List.of(rating);
        Page<Rating> ratings = new PageImpl<>(ratingList, pageable, ratingList.size());

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratings);
        when(ratingMapper.toDto(any(Rating.class))).thenReturn(ratingRes);
        when(ratingMapper.toPagingResult(any(Page.class), any())).thenCallRealMethod();

        PagingRes<RatingRes> result = ratingServiceImpl.getAllRatings(0, 10, "asc", "id");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Test getAllRatings with page size higher than 100 to check if it defaults to 100")
    void testGetAllRatingsWithPageSizeHigherThan100() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));

        List<Rating> ratingList = new ArrayList<>();

        for(int i = 1; i <= 100; i++) {
            Rating rating = new Rating();
            rating.setId((long) i);
            rating.setScore(4.5);
            rating.setComment("Great product!");
            ratingList.add(rating);
        }

        Page<Rating> ratings = new PageImpl<>(ratingList, pageable, 100);

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratings);
        when(ratingMapper.toPagingResult(any(Page.class), any())).thenCallRealMethod();

        PagingRes<RatingRes> result = ratingServiceImpl.getAllRatings(0, 10000, "asc", "id");

        assertNotNull(result);
        assertEquals(100, result.getTotalElements());
        assertEquals(100, result.getSize());
    }

    @Test
    @DisplayName("Test getAllRatings with page size lower than 0 to check if it defaults to 10")
    void testGetAllRatingsWithPageSizeLowerThan0() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<Rating> ratingList = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            Rating rating = new Rating();
            rating.setId((long) i);
            rating.setScore(4.5);
            rating.setComment("Great product!");
            ratingList.add(rating);
        }

        Page<Rating> ratings = new PageImpl<>(ratingList, pageable, 10);

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratings);
        when(ratingMapper.toPagingResult(any(Page.class), any())).thenCallRealMethod();

        PagingRes<RatingRes> result = ratingServiceImpl.getAllRatings(0, -1, "asc", "id");

        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(10, result.getSize());
    }

    @Test
    @DisplayName("Test getAllRatings with page no lower than 0 to check if it defaults to 0")
    void testGetAllRatingsWithPageNoLowerThan0() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setScore(4.5);
        rating.setComment("Great product!");

        List<Rating> ratingList = List.of(rating);
        Page<Rating> ratings = new PageImpl<>(ratingList, pageable, ratingList.size());

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratings);
        when(ratingMapper.toPagingResult(any(Page.class), any())).thenCallRealMethod();

        PagingRes<RatingRes> result = ratingServiceImpl.getAllRatings(-1, 10, "asc", "id");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getPage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("sortingTestCases")
    void testGetAllRatingsWithDifferentSortParams(String testName, String sortDir, String sortBy) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<Rating> ratingList = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            Rating rating = new Rating();
            rating.setId((long) i);
            rating.setScore(4.5);
            rating.setComment("Great product!");
            ratingList.add(rating);
        }
        Page<Rating> ratings = new PageImpl<>(ratingList, pageable, ratingList.size());

        // Mock the behavior of the repository and mapper
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratings);
        when(ratingMapper.toPagingResult(any(Page.class), any())).thenCallRealMethod();

        PagingRes<RatingRes> result = ratingServiceImpl.getAllRatings(0, 10, sortDir, sortBy);

        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getPage());
    }

}
