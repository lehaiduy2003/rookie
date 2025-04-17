package com.example.assignment.service;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;

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
    ProductRes createProduct(ProductCreationReq productCreationReq);

    /**
     * Updates an existing product.
     * This method is used to update the product details but not update the category.
     *
     * @param id the id of the product to update
     * @param productUpdatingReq the request object containing product update details
     * @return the updated product
     */
    ProductRes updateProductById(Long id, ProductUpdatingReq productUpdatingReq);

    /**
     * Retrieves a product detail by id.
     *
     * @param id the id of the product to retrieve
     * @return the retrieved product
     */
    ProductDetailRes getProductById(Long id);

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
    ProductRes updateProductCategoryById(Long id, Long categoryId);

    /**
     * Retrieves a product using pagination.
     *
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingRes<ProductRes> getProducts(Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Retrieves a product using pagination and filter by category id.
     *
     * @param categoryId the id of the category to filter by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingRes<ProductRes> getProductsByCategoryId(Long categoryId, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Retrieves a product using pagination and filter by name.
     *
     * @param name the name of the product to filter by
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return the retrieved product
     */
    PagingRes<ProductRes> getProductsByName(String name, Integer pageNo, Integer pageSize, String sortDir, String sortBy);
}
