package com.example.assignment.specification;

import com.example.assignment.entity.Category;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering Category entities.
 * This class provides static methods to create specifications based on various criteria.
 */
public class CategorySpecification {

    private CategorySpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a specification to filter categories by name.
     * @param name the name to filter by
     * @return a specification that filters categories by name
     */
    public static Specification<Category> hasName(String name) {
        return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Creates a specification to filter categories by parent ID.
     * @param parentId the parent ID to filter by
     * @return a specification that filters categories by parent ID
     */
    public static Specification<Category> hasParentId(Long parentId) {
        return (root, query, criteriaBuilder) ->
            parentId == null ? null : criteriaBuilder.equal(root.get("parent").get("id"), parentId);
    }

    /**
     * Creates a specification to filter categories by description.
     * @param description the description to filter by
     * @return a specification that filters categories by description
     */
    public static Specification<Category> hasDescription(String description) {
        return (root, query, criteriaBuilder) ->
            description == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

}
