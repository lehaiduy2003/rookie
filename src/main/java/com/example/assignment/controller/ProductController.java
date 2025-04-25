package com.example.assignment.controller;

import com.example.assignment.dto.request.*;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
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
        @Valid @ModelAttribute ProductFilterReq filter,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Get products by filter: {}", filter);
        PagingRes<ProductRes> products = productService.getProducts(filter, pageNo, pageSize, sortDir, sortBy);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductRes> createProduct(@Valid @RequestBody ProductCreationReq productCreationReq) {
        ProductRes createdProduct = productService.createProduct(productCreationReq);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductRes> updateProductById(@PathVariable Long id, @Valid @RequestBody ProductUpdatingReq productUpdatingReq) {
        // Permission check is handled in the service layer
        ProductRes updatedProduct = productService.updateProductById(id, productUpdatingReq);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/category/{categoryId}")
    public ResponseEntity<ProductRes> updateProductCategoryById(@PathVariable Long id, @PathVariable Long categoryId) {
        ProductRes updatedProduct = productService.updateProductCategoryById(id, categoryId);
        return ResponseEntity.ok(updatedProduct);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFeaturedProduct(@PathVariable Long id, @RequestParam(defaultValue = "false") Boolean featured) {
        productService.updateToFeaturedProduct(id, featured);
        return ResponseEntity.noContent().build();
    }
}
