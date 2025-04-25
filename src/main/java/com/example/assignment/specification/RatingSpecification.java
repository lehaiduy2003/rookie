package com.example.assignment.specification;

import com.example.assignment.entity.Rating;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering ratings.
 * This class contains static methods that return Specifications
 * for filtering ratings based on various criteria.
 * This class's constructor is private to prevent instantiation.
 */
public class RatingSpecification {
    private RatingSpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Specification to filter ratings by rating value.
     * @param customerId the customer ID to filter by
     * @return a Specification that filters ratings by customer ID
     */
    public static Specification<Rating> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    /**
     * Specification to filter ratings by product ID.
     * @param productId the product ID to filter by
     * @return a Specification that filters ratings by product ID
     */
    public static Specification<Rating> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("product").get("id"), productId);
    }
}
