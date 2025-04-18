package com.example.assignment.constant;

public final class SecurityURL {
    // Public URLs that are accessible without authentication
    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",
        "/swagger-ui/**",
        "/api/v1/categories/tree",                // getCategoryTree
        "/api/v1/categories/tree/{categoryId}",   // getCategoryTreeByParentId
        "/api/v1/products/{id}",                  // getProduct
        "/api/v1/products/search",                // searchProducts
        "/api/v1/products/category/{categoryId}", // getProductsByCategoryId
        "/api/v1/ratings/product/{productId}",    // getRatingsByProductId
        "/api/v1/users/{id}"                      // getUserById
    };
}
