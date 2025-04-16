package com.example.assignment.repository;
import com.example.assignment.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Category entities.
 * This interface extends JpaRepository to provide CRUD operations.
 * It includes methods for finding categories by parent ID and name.
 * The methods are used to retrieve categories based on their relationships and properties.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Find categories by parent ID.
     * @param parentId the parent ID
     * @return a list of categories with the given parent ID
     */
    List<Category> findCategoriesByParent_Id(Long parentId);

    /**
     * Find categories by name, ignoreCase.
     * @param name the name to search for
     * @return a category with the given name, ignoring case
     */
    Category findCategoryByNameContainingIgnoreCase(String name);
}
