package com.example.assignment.controller;

import com.example.assignment.AssignmentApplicationTests;
import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.ErrorRes;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ProductControllerTest extends AssignmentApplicationTests {

    @Test
    @DisplayName("Get product by ID - Public access")
    void getProductById_Success() {
        ResponseEntity<ProductDetailRes> response = restTemplate.exchange(
            getUrl("/api/v1/products/1"),
            HttpMethod.GET,
            null,
            ProductDetailRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Get products with pagination and filtering")
    void getProducts_WithFiltering_Success() {
        ResponseEntity<PagingRes<ProductRes>> response = restTemplate.exchange(
            getUrl("/api/v1/products?pageSize=5&sortBy=price&sortDir=desc&minPrice=10"),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertTrue(response.getBody().getTotalElements() > 0);
    }

    @Test
    @DisplayName("Create product as admin - Success")
    void createProduct_AsAdmin_Success() {

        String accessToken = authenticateAndGetToken("admin@example.com", "admin123");

        ProductCreationReq newProduct = ProductCreationReq.builder()
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .quantity(10)
            .categoryId(1L)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ProductRes> response = restTemplate.exchange(
            getUrl("/api/v1/products"),
            HttpMethod.POST,
            new HttpEntity<>(newProduct, headers),
            ProductRes.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals(29.99, response.getBody().getPrice());
    }

    @Test
    @DisplayName("Customer cannot create product")
    void createProduct_AsCustomer_Forbidden() {
        String customerToken = authenticateAndGetToken("customer@example.com", "customer123");

        ProductCreationReq newProduct = ProductCreationReq.builder()
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .quantity(10)
            .categoryId(1L)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            getUrl("/api/v1/products"),
            HttpMethod.POST,
            new HttpEntity<>(newProduct, headers),
            String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Update product as admin")
    void updateProduct_AsAdmin_Success() {
        String adminToken = authenticateAndGetToken("admin@example.com", "admin123");

        ProductUpdatingReq updateReq = ProductUpdatingReq.builder()
            .name("Updated Product")
            .description("Updated Description")
            .price(39.99)
            .quantity(20)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ProductRes> response = restTemplate.exchange(
            getUrl("/api/v1/products/1"),
            HttpMethod.PUT,
            new HttpEntity<>(updateReq, headers),
            ProductRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Product", response.getBody().getName());
        assertEquals(39.99, response.getBody().getPrice());
    }

    @Test
    @DisplayName("Delete product as admin")
    void deleteProduct_AsAdmin_Success() {
        String adminToken = authenticateAndGetToken("admin@example.com", "admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        // First confirm product exists
        ResponseEntity<ProductDetailRes> checkResponse = restTemplate.exchange(
            getUrl("/api/v1/products/2"),
            HttpMethod.GET,
            null,
            ProductDetailRes.class
        );
        assertEquals(HttpStatus.OK, checkResponse.getStatusCode());

        // Delete the product
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            getUrl("/api/v1/products/3"),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify product is gone
        ResponseEntity<String> verifyResponse = restTemplate.exchange(
            getUrl("/api/v1/products/3"),
            HttpMethod.GET,
            null,
            String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, verifyResponse.getStatusCode());
    }

    @Test
    @DisplayName("Update product category as admin")
    void updateProductCategory_AsAdmin_Success() {
        String adminToken = authenticateAndGetToken("admin@example.com", "admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<ProductRes> response = restTemplate.exchange(
            getUrl("/api/v1/products/1/category/2"),
            HttpMethod.PATCH,
            new HttpEntity<>(headers),
            ProductRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Update product to featured as admin")
    void updateProductFeatured_AsAdmin_Success() {
        String adminToken = authenticateAndGetToken("admin@example.com", "admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<Void> response = restTemplate.exchange(
            getUrl("/api/v1/products/1?featured=true"),
            HttpMethod.PATCH,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify the product is now featured
        ResponseEntity<ProductDetailRes> verifyResponse = restTemplate.exchange(
            getUrl("/api/v1/products/1"),
            HttpMethod.GET,
            null,
            ProductDetailRes.class
        );

        assertNotNull(verifyResponse.getBody());
        assertTrue(verifyResponse.getBody().getFeatured());
    }
}
