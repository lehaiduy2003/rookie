package com.example.assignment.service;

import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.request.CategoryFilterReq;
import com.example.assignment.dto.response.CategoryRes;
import com.example.assignment.dto.response.CategoryTreeRes;

import java.util.List;

/**
 * Service class for managing categories.
 * This class is responsible for handling business logic related to categories,
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param categoryCreationReq the request object containing category creation details
     * @return the created Category object
     */
    CategoryRes createCategory(CategoryCreationReq categoryCreationReq);

    /**
     * Updates an existing category.
     * It can update the parent ID category as well.
     *
     * @param categoryId the ID of the category to be updated
     * @param categoryCreationReq the request object containing updated category details
     * @return the updated Category object
     */
    CategoryRes updateCategoryById(Long categoryId, CategoryCreationReq categoryCreationReq);

    /**
     * Deletes a category.
     * This method will delete the category with the specified ID.
     * If the category has child categories, the deletion will not be performed.
     * @param categoryId the ID of the category to be deleted
     */
    void deleteCategoryById(Long categoryId);

    /**
     * Forcefully deletes a category by its ID.
     * This method will delete the category with the specified ID, regardless of whether it has child categories.
     * @param categoryId the ID of the category to be forcefully deleted
     */
    void forceDeleteCategoryById(Long categoryId);

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId the ID of the category to be retrieved
     * @return the Category object with the specified ID
     */
    CategoryRes getCategoryById(Long categoryId);


    /**
     * Retrieves a tree structure of categories by parent ID.
     * @param categoryId the root category ID for the tree structure
     * @return a tree structure of categories under the specified parent ID
     */
    List<CategoryTreeRes> getCategoryTreeByParentId(Long categoryId);


    /**
     * Retrieves a tree structure of all categories.
     * @return a tree structure of categories
     */
    List<CategoryTreeRes> getCategoryTree();

    /**
     * Retrieves a list of top-level categories.
     * This method will return categories that do not have a parent category.
     * @return a list of top-level categories
     */
    List<CategoryRes> getTopLevelCategories();

    /**
     * Retrieves a list of all categories.
     * @return a list of all categories
     */
    List<CategoryRes> getAllCategories(CategoryFilterReq categoryFilterReq);

}
