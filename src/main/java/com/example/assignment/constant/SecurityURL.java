package com.example.assignment.constant;

public final class SecurityURL {
    // Public URLs that are accessible without authentication
    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",                    // auth
        "/swagger-ui/**",                     // swagger-ui
        "/v3/api-docs/**",                    // api-docs
        "/api/v1/categories/parents",          // getTopLevelCategories
        "/api/v1/categories/tree",            // getCategoryTree
        "/api/v1/categories/tree/*",          // getCategoryTreeByParentId
        "/api/v1/categories/*",               // getCategoryById
        "/api/v1/products/*",                 // getProduct
        "/api/v1/users/*",                    // getUserById
        "/api/v1/products",                   // getProducts
        "/api/v1/products/search",            // searchProducts
        "/api/v1/products/category/*",        // getProductsByCategoryId
        "/api/v1/ratings/product/*",          // getRatingsByProductId
    };
}
