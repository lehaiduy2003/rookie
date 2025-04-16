package com.example.assignment.repository;
import com.example.assignment.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Find categories by parent ID.
     * @param parentId the parent ID
     * @return a list of categories with the given parent ID
     */
    List<Category> findCategoriesByParent_Id(Long parentId);
}
