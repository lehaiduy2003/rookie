package com.example.assignment.service.impl;

import com.example.assignment.dto.request.CategoryDtoReq;
import com.example.assignment.dto.response.CategoryDtoRes;
import com.example.assignment.dto.response.CategoryTreeRes;
import com.example.assignment.entity.Category;
import com.example.assignment.mapper.CategoryMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDtoRes createCategory(CategoryDtoReq categoryDtoReq) {
        Long parentId = categoryDtoReq.getParentId();
        Category category = categoryMapper.toEntity(categoryDtoReq);
        // check if parentId is not null and exists in the database to set the parent category
        setParentCategory(category, parentId);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDtoRes(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDtoRes updateCategoryById(Long categoryId, CategoryDtoReq categoryDtoReq) {
        // check if parentId is not equal to categoryId
        Long parentId = categoryDtoReq.getParentId();
        String name = categoryDtoReq.getName();
        String description = categoryDtoReq.getDescription();
        if(Objects.equals(parentId, categoryId)) {
            throw new IllegalArgumentException("Parent ID cannot be the same as Category ID");
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("Category not found")
        );
        // update category details if they are not null
        category.setName(categoryDtoReq.getName() != null ? name : category.getName());
        category.setDescription(categoryDtoReq.getDescription() != null ? description : category.getDescription());
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
    public CategoryDtoRes getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Map the category entity to DTO
        return CategoryDtoRes.builder()
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

}
