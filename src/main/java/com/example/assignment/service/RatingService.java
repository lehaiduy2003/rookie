package com.example.assignment.service;

import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.request.RatingUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.RatingRes;

/**
 * Service interface for managing ratings.
 * This interface defines methods for creating, retrieving, updating, and deleting ratings.
 * It is used for rating management.
 */
public interface RatingService {
    /**
     * Creates a new rating.
     * @param ratingCreationReq the request object containing rating creation details
     * @return the created Rating object
     */
    RatingRes createRating(RatingCreationReq ratingCreationReq);

    /**
     * Retrieves a rating by its ID.
     * @param id the ID of the rating to be retrieved
     * @return the Rating object with the specified ID
     */
    RatingRes getRatingById(Long id);

    /**
     * Retrieves all ratings with pagination.
     * @param pageNo the page number to retrieve
     * @param pageSize the number of ratings per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a pageable result of ratings
     */
    PagingRes<RatingRes> getAllRatings(Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Retrieves ratings by product ID with pagination.
     * @param productId the ID of the product to filter ratings by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of ratings per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a pageable result of ratings for the specified product
     */
    PagingRes<RatingRes> getRatingsByProductId(Long productId, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Retrieves ratings by customer ID with pagination.
     * @param customerId the ID of the customer to filter ratings by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of ratings per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a pageable result of ratings for the specified customer
     */
    PagingRes<RatingRes> getRatingsByCustomerId(Long customerId, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Updates an existing rating.
     * @param id the ID of the rating to be updated
     * @param ratingUpdatingReq the request object containing rating update details
     * @return the updated Rating object
     */
    RatingRes updateRating(Long id, RatingUpdatingReq ratingUpdatingReq);

    /**
     * Deletes a rating by its ID.
     * @param id the ID of the rating to be deleted
     */
    void deleteRating(Long id);
}
