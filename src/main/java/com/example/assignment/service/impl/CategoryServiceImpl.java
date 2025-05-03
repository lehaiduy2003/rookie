package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.request.CategoryFilterReq;
import com.example.assignment.dto.response.CategoryRes;
import com.example.assignment.dto.response.CategoryTreeRes;
import com.example.assignment.entity.Category;
import com.example.assignment.mapper.CategoryMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.service.CategoryService;
import com.example.assignment.specification.CategorySpecification;
import com.example.assignment.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
@RequiredArgsConstructor
@Logging
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryRes> getTopLevelCategories() {
        // Fetch root categories (categories with no parent)
        List<Category> rootCategories = categoryRepository.findCategoriesByParentIsNull();
        return rootCategories.stream()
            // Map each root category to CategoryRes
            .map(categoryMapper::toDtoRes)
            .toList();
    }

    @Override
    @Transactional
    public CategoryRes createCategory(CategoryCreationReq categoryCreationReq) {
        Long parentId = categoryCreationReq.getParentId();
        Category category = categoryMapper.toEntity(categoryCreationReq);
        // check if parentId is not null and exists in the database to set the parent category
        setParentCategory(category, parentId);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDtoRes(savedCategory);
    }

    @Override
    @Transactional
    public CategoryRes updateCategoryById(Long categoryId, CategoryCreationReq categoryCreationReq) {
        // check if parentId is not equal to categoryId
        Long parentId = categoryCreationReq.getParentId();
        String name = categoryCreationReq.getName();
        String description = categoryCreationReq.getDescription();
        if(Objects.equals(parentId, categoryId)) {
            throw new IllegalArgumentException("Parent ID cannot be the same as Category ID");
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("Category not found")
        );
        // update category details if they are not null
        category.setName(categoryCreationReq.getName() != null ? name : category.getName());
        category.setDescription(categoryCreationReq.getDescription() != null ? description : category.getDescription());
        // check if parentId is not null and exists in the database to set the parent category
        setParentCategory(category, parentId);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDtoRes(updatedCategory);
    }

    @Override
    @Transactional
    public void forceDeleteCategoryById(Long categoryId) {
        if(isCategoryNotExists(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        if(isCategoryNotExists(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }
        // If the category has child categories, stop the deletion
        if (hasChildCategories(categoryId)) {
            throw new IllegalArgumentException("Category has child categories and cannot be deleted");
        }
        // Delete the category
        categoryRepository.deleteById(categoryId);
    }
    @Override
    public CategoryRes getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Map the category entity to DTO
        return CategoryRes.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .parentId(category.getParent() != null ? category.getParent().getId() : null)
            .build();
    }

    @Override
    public List<CategoryTreeRes> getCategoryTreeByParentId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Build the tree structure starting from the given category
        CategoryTreeRes categoryTree = buildCategoryTree(category);

        // Return the tree as a single-element list
        return List.of(categoryTree);
    }

    @Override
    public List<CategoryTreeRes> getCategoryTree() {
        // Fetch root categories (categories with no parent)
        List<Category> rootCategories = categoryRepository.findCategoriesByParent_Id(null);
        return rootCategories.stream()
            // For each root category, build the tree structure
            .map(this::buildCategoryTree)
            .toList();
    }

    private boolean isCategoryNotExists(Long categoryId) {
        return !categoryRepository.existsById(categoryId);
    }

    private void setParentCategory(Category category, Long parentId) {
        if (parentId != null) {
            Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parentCategory);
        }
    }

    private boolean hasChildCategories(Long categoryId) {
        return !categoryRepository.findCategoriesByParent_Id(categoryId).isEmpty();
    }

    private CategoryTreeRes buildCategoryTree(Category category) {
        // Map the current category to CategoryTreeRes
        CategoryTreeRes categoryTreeRes = CategoryTreeRes.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .build();

        // Fetch child categories and recursively build the tree
        List<CategoryTreeRes> subCategoryTrees = category.getSubCategories().stream()
            .map(this::buildCategoryTree)
            .toList();

        // Set the subcategories in the tree response
        categoryTreeRes.setSubCategories(subCategoryTrees);

        return categoryTreeRes;
    }

    @Override
    public List<CategoryRes> getAllCategories(CategoryFilterReq categoryFilterReq) {
        Specification<Category> spec = new SpecificationBuilder<Category>()
            .addIfNotNull(categoryFilterReq.getName(), CategorySpecification::hasName)
            .addIfNotNull(categoryFilterReq.getParentId(), CategorySpecification::hasParentId)
            .addIfNotNull(categoryFilterReq.getDescription(), CategorySpecification::hasDescription)
            .build();
        // Fetch all categories from the database
        List<Category> categories = categoryRepository.findAll(spec);
        return categories.stream()
            // Map each category to CategoryRes
            .map(categoryMapper::toDtoRes)
            .toList();
    }
}
