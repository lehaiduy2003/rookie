package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.ProductService;
import com.example.assignment.service.impl.paging.ProductPagingServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductPagingServiceImpl productPagingService;


    @Override
    @Transactional
    public ProductRes createProduct(ProductCreationReq productCreationReq) {
        Long categoryId = productCreationReq.getCategoryId();
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = productMapper.toEntity(productCreationReq);
        product.setCategory(category);

        // Validate product before saving
        validateProduct(product);

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
    @Transactional
    public ProductRes updateProductById(Long id, ProductUpdatingReq productUpdatingReq) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if a user is authorized to update this product
        checkUpdatePermission(product);

        updateProductDetails(product, productUpdatingReq);

        // Validate product before saving
        validateProduct(product);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    /**
     * Checks if the current user has permission to update the product.
     * Admins can update any product, while customers can only update products they created.
     *
     * @param product the product to check permissions for
     * @throws org.springframework.security.access.AccessDeniedException if the user doesn't have permission
     */
    private void checkUpdatePermission(Product product) {
        // Get a current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User currentUser)) {
            throw new AccessDeniedException("Authentication required");
        }

        // Admins can update any product
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        // Customers can only update products they created
        if (product.getCreatedBy() == null || !product.getCreatedBy().equals(currentUser)) {
            throw new AccessDeniedException("You can only update products you created");
        }
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if a product has ratings
        if (product.getRatings() != null && !product.getRatings().isEmpty()) {
            throw new IllegalStateException("Product with ratings cannot be deleted. Use archive option instead.");
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductRes updateProductCategoryById(Long id, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setCategory(category);

        // Validate product before saving
        validateProduct(product);

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

    @Override
    @Transactional
    public void updateToFeaturedProduct(Long id, Boolean isFeatured) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Only admin can update the featured attribute
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User currentUser)) {
            throw new AccessDeniedException("Authentication required");
        }

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can update featured status");
        }

        product.setFeatured(isFeatured);

        // Validate product before saving
        validateProduct(product);

        productRepository.save(product);
    }

    @Override
    public PagingRes<ProductRes> getFeaturedProducts(boolean featured, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return productPagingService.getFeaturedProducts(featured, pageNo, pageSize, sortDir, sortBy);
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

    /**
     * Validates product data to ensure it meets business rules.
     * 
     * @param product the product to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProduct(Product product) {
        // Validate avgRating (must be between 0 and 5)
        if (product.getAvgRating() < 0 || product.getAvgRating() > 5) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5");
        }

        // Validate ratingCount (must not be negative)
        if (product.getRatingCount() < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }

        // Validate quantity (must not be negative)
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }

        // Validate price (must not be negative)
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        // Validate name (must not be null)
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        // Validate category (must not be null)
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product category cannot be null");
        }
    }
}
