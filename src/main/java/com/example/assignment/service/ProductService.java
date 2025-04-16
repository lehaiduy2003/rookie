package com.example.assignment.service;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.ProductDtoRes;

/**
 * ProductService interface for managing product (CRUD) operations.
 * This interface defines methods for creating, updating, and deleting products.
 * It is used for product management.
 */
public interface ProductService {
    /**
     * Creates a new product.
     *
     * @param productCreationReq the request object containing product creation details
     * @return the created product
     */
    ProductDtoRes createProduct(ProductCreationReq productCreationReq);

    /**
     * Updates an existing product.
     * this method is used to update the product details but not update the category.
     *
     * @param id the id of the product to update
     * @param productUpdatingReq the request object containing product update details
     * @return the updated product
     */
    ProductDtoRes updateProductById(Long id, ProductUpdatingReq productUpdatingReq);

    /**
     * Retrieves a product detail by id.
     *
     * @param id the id of the product to retrieve
     * @return the retrieved product
     */
    ProductDtoRes getProductById(Long id);

    /**
     * Deletes a product by id.
     *
     * @param id the id of the product to delete
     */
    void deleteProductById(Long id);

    /**
     * Updates the category of a product by product's id.
     *
     * @param id the id of the product to update
     * @param categoryId the id of the new category
     * @return the updated product with a new category
     */
    ProductDtoRes updateProductCategoryById(Long id, Long categoryId);

    /**
     * Retrieves a product using pagination.
     *
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingResult<ProductDtoRes> getProducts(Integer pageNo, Integer pageSize, String sortBy);

    /**
     * Retrieves a product using pagination and filter by category id.
     *
     * @param categoryId the id of the category to filter by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingResult<ProductDtoRes> getProductsByCategoryId(Long categoryId, Integer pageNo, Integer pageSize, String sortBy);

    /**
     * Retrieves a product using pagination and filter by name.
     *
     * @param name the name of the product to filter by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingResult<ProductDtoRes> getProductsByName(String name, Integer pageNo, Integer pageSize, String sortBy);
}
