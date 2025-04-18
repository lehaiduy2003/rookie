package com.example.assignment.controller;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailRes> getProduct(@PathVariable Long id) {
        ProductDetailRes products = productService.getProductById(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<PagingRes<ProductRes>> getProducts(
        @RequestParam(defaultValue = "false") Boolean featured,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if (Boolean.TRUE.equals(featured)) {
            PagingRes<ProductRes> featuredProducts = getFeaturedProducts(pageNo, pageSize, sortBy, sortDir);
            return ResponseEntity.ok(featuredProducts);
        } else {
            PagingRes<ProductRes> products = productService.getProducts(pageNo, pageSize, sortDir, sortBy);
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole({'ADMIN', 'CUSTOMER'})")
    public ResponseEntity<ProductRes> createProduct(@RequestBody ProductCreationReq productCreationReq) {
        ProductRes createdProduct = productService.createProduct(productCreationReq);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole({'ADMIN', 'CUSTOMER'})")
    public ResponseEntity<ProductRes> updateProductById(@PathVariable Long id, @RequestBody ProductUpdatingReq productUpdatingReq) {
        ProductRes updatedProduct = productService.updateProductById(id, productUpdatingReq);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/category/{categoryId}")
    @PreAuthorize("hasRole({'ADMIN', 'CUSTOMER'})")
    public ResponseEntity<ProductRes> updateProductCategoryById(@PathVariable Long id, @PathVariable Long categoryId) {
        ProductRes updatedProduct = productService.updateProductCategoryById(id, categoryId);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagingRes<ProductRes>> getProductsByCategoryId(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PagingRes<ProductRes> products = productService.getProductsByCategoryId(categoryId, pageNo, pageSize, sortDir, sortBy);
        return ResponseEntity.ok(products);
    }

    @GetMapping("search")
    public ResponseEntity<PagingRes<ProductRes>> searchProducts(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PagingRes<ProductRes> products = productService.getProductsByName(name, pageNo, pageSize, sortDir, sortBy);
        return ResponseEntity.ok(products);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFeaturedProduct(@PathVariable Long id, @RequestParam(defaultValue = "false") Boolean featured) {
        productService.updateToFeaturedProduct(id, featured);
        return ResponseEntity.noContent().build();
    }

    private PagingRes<ProductRes> getFeaturedProducts(
        Integer pageNo,
        Integer pageSize,
        String sortBy,
        String sortDir
    ) {
        return productService.getFeaturedProducts(true, pageNo, pageSize, sortDir, sortBy);
    }
}
