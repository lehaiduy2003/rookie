package com.example.assignment.controller;

import com.example.assignment.dto.request.CategoryDtoReq;
import com.example.assignment.dto.response.CategoryDtoRes;
import com.example.assignment.dto.response.CategoryTreeRes;
import com.example.assignment.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDtoRes> createCategory(@Valid @RequestBody CategoryDtoReq categoryDtoReq) {
        try {
            CategoryDtoRes createdCategory = categoryService.createCategory(categoryDtoReq);
            return ResponseEntity.status(201).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDtoRes> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDtoReq categoryDtoReq) {
        try {
            CategoryDtoRes updatedCategory = categoryService.updateCategoryById(categoryId, categoryDtoReq);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{categoryId}/force")
    public ResponseEntity<Void> forceDeleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.forceDeleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDtoRes> getCategoryById(@PathVariable Long categoryId) {
        try {
            CategoryDtoRes category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeRes>> getCategoryTree() {
        try {
            List<CategoryTreeRes> categoryTree = categoryService.getCategoryTree();
            return ResponseEntity.ok(categoryTree);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tree/{categoryId}")
    public ResponseEntity<List<CategoryTreeRes>> getCategoryTreeByParentId(@PathVariable Long categoryId) {
        try {
            List<CategoryTreeRes> categoryTree = categoryService.getCategoryTreeByParentId(categoryId);
            return ResponseEntity.ok(categoryTree);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
