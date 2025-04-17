package com.example.assignment.service.impl;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.ProductService;
import com.example.assignment.service.impl.paging.ProductPagingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductPagingServiceImpl productPagingService;


    @Override
    public ProductRes createProduct(ProductCreationReq productCreationReq) {
        Long categoryId = productCreationReq.getCategoryId();
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = productMapper.toEntity(productCreationReq);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toDto(savedProduct);

    }

    @Override
    public ProductDetailRes getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return productMapper.toDetailsDto(product);
    }


    @Override
    public ProductRes updateProductById(Long id, ProductUpdatingReq productUpdatingReq) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        updateProductDetails(product, productUpdatingReq);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public ProductRes updateProductCategoryById(Long id, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setCategory(category);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public PagingRes<ProductRes> getProducts(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return productPagingService.getMany(pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new IllegalArgumentException("No products found");
        }
    }

    @Override
    public PagingRes<ProductRes> getProductsByCategoryId(Long categoryId, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return productPagingService.getById(categoryId, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new IllegalArgumentException("No products found");
        }
    }

    @Override
    public PagingRes<ProductRes> getProductsByName(String name, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return productPagingService.getByCriteria(name, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new IllegalArgumentException("No products found");
        }
    }

    private void updateProductDetails(Product product, ProductUpdatingReq productUpdatingReq) {
        if (productUpdatingReq.getName() != null) {
            product.setName(productUpdatingReq.getName());
        }
        if (productUpdatingReq.getDescription() != null) {
            product.setDescription(productUpdatingReq.getDescription());
        }
        if (productUpdatingReq.getPrice() != null) {
            product.setPrice(productUpdatingReq.getPrice());
        }
        if (productUpdatingReq.getQuantity() != null) {
            product.setQuantity(productUpdatingReq.getQuantity());
        }
        if (productUpdatingReq.getImageUrl() != null) {
            product.setImageUrl(productUpdatingReq.getImageUrl());
        }
        if (productUpdatingReq.getIsActive() != null) {
            product.setIsActive(productUpdatingReq.getIsActive());
        }
    }
}
