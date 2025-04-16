package com.example.assignment.controller;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.ProductDtoRes;
import com.example.assignment.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDtoRes> getAllProducts(@PathVariable Long id) {
        ProductDtoRes products = productService.getProductById(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<PagingResult<ProductDtoRes>> getPageableProducts(
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy
    ) {
        PagingResult<ProductDtoRes> products = productService.getProducts(pageNo, pageSize, sortBy);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDtoRes> createProduct(@RequestBody ProductCreationReq productCreationReq) {
        ProductDtoRes createdProduct = productService.createProduct(productCreationReq);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDtoRes> updateProductById(@PathVariable Long id, @RequestBody ProductUpdatingReq productUpdatingReq) {
        ProductDtoRes updatedProduct = productService.updateProductById(id, productUpdatingReq);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/category/{categoryId}")
    public ResponseEntity<ProductDtoRes> updateProductCategoryById(@PathVariable Long id, @PathVariable Long categoryId) {
        ProductDtoRes updatedProduct = productService.updateProductCategoryById(id, categoryId);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagingResult<ProductDtoRes>> getProductsByCategoryId(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy
    ) {
        PagingResult<ProductDtoRes> products = productService.getProductsByCategoryId(categoryId, pageNo, pageSize, sortBy);
        return ResponseEntity.ok(products);
    }

    @GetMapping("search")
    public ResponseEntity<PagingResult<ProductDtoRes>> searchProducts(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy
    ) {
        PagingResult<ProductDtoRes> products = productService.getProductsByName(name, pageNo, pageSize, sortBy);
        return ResponseEntity.ok(products);
    }
}
