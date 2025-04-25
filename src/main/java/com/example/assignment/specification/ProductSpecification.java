package com.example.assignment.specification;

import com.example.assignment.entity.Product;
import org.springframework.data.jpa.domain.Specification;


/**
 * Specification class for filtering products.
 * This class contains static methods that return Specifications
 * For filtering products based on various criteria.
 * This class's constructor is private to prevent instantiation.
 */
public class ProductSpecification {
    private ProductSpecification() {
        // Private constructor to prevent instantiation
    }
    /**
     * Specification to filter products by name.
     * @param name the name to filter by
     * @return a Specification that filters products by name
     */
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Specification to filter products by featured status.
     * @param featured the featured status to filter by
     * @return a Specification that filters products by featured status
     */
    public static Specification<Product> isFeatured(Boolean featured) {
        return (root, query, criteriaBuilder) ->
            featured == null ? null : criteriaBuilder.equal(root.get("featured"), featured);
    }

    /**
     * Specification to filter products by categoryId.
     * @param categoryId the category ID to filter by
     * @return a Specification that filters products by active status
     */
    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
            categoryId == null ? null : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }
    /**
     * Specification to filter products by active status.
     * @param active the active status to filter by
     * @return a Specification that filters products by active status
     */
    public static Specification<Product> hasIsActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
            active == null ? null : criteriaBuilder.equal(root.get("isActive"), active);
    }
    /**
     * Specification to filter products by rating range.
     * @param minRating the lower bound of the rating range
     * @param maxRating the upper bound of the rating range
     * @return a Specification that filters products by rating
     */
    public static Specification<Product> hasRatingBetween(Double minRating, Double maxRating) {
        return getBoundarySpec(minRating, maxRating, "avgRating");
    }

    /**
     * Specification to filter products by price range.
     * @param minPrice the lower bound of the price range
     * @param maxPrice the upper bound of the price range
     * @return a Specification that filters products by price
     */
    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return getBoundarySpec(minPrice, maxPrice, "price");
    }

    private static Specification<Product> getBoundarySpec(Double lowerBound, Double higherBound, String target) {
        return (root, query, criteriaBuilder) -> {
            if (lowerBound == null && higherBound == null) {
                return null;
            } else if (lowerBound != null && higherBound != null) {
                return criteriaBuilder.between(root.get(target), lowerBound, higherBound);
            } else if (lowerBound != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(target), lowerBound);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get(target), higherBound);
            }
        };
    }
}
