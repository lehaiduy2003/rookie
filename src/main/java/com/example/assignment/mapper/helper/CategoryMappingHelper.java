package com.example.assignment.mapper.helper;

import com.example.assignment.entity.Category;
import com.example.assignment.repository.CategoryRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class CategoryMappingHelper {
    private final CategoryRepository categoryRepository;

    public CategoryMappingHelper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Named("mapCategoryFromId")
    public Category map(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
