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
     * @param rating the lower bound of the rating range
     * @return a Specification that filters products by rating
     */
    public static Specification<Product> hasRating(Double rating) {
        return (root, query, criteriaBuilder) -> {
            if (rating == null) {
                return null;
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("avgRating"), rating);
            }
        };
    }

    /**
     * Specification to filter products by price range.
     * @param minPrice the lower bound of the price range
     * @param maxPrice the upper bound of the price range
     * @return a Specification that filters products by price
     */
    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        String target = "price";
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            } else if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get(target), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(target), minPrice);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get(target), maxPrice);
            }
        };
    }
}
