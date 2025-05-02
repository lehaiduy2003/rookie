package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductFilterReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.BaseRepository;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.ProductService;
import com.example.assignment.specification.ProductSpecification;
import com.example.assignment.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Logging
public class ProductServiceImpl extends PagingServiceImpl<ProductRes, Product, Long> implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    protected BaseRepository<Product, Long> getRepository() {
        return productRepository;
    }

    @Override
    protected ProductRes convertToDto(Product entity) {
        return productMapper.toDto(entity);
    }

    @Override
    protected PagingRes<ProductRes> toPagingResult(Page<Product> page, Function<Product, ProductRes> converter) {
        return productMapper.toPagingResult(page, converter);
    }

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

        // Validate product before saving
        validateProduct(product);

        updateProductDetails(product, productUpdatingReq);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
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
    @Transactional
    public void updateToFeaturedProduct(Long id, Boolean isFeatured) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setFeatured(isFeatured);

        // Validate product before saving
        validateProduct(product);

        productRepository.save(product);
    }

    @Override
    public PagingRes<ProductRes> getProducts(ProductFilterReq filterReq, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            Specification<Product> spec = new SpecificationBuilder<Product>()
                .addIfNotNull(filterReq.getName(), ProductSpecification::hasName)
                .addIfNotNull(filterReq.getIsActive(), ProductSpecification::hasIsActive)
                .addIfNotNull(filterReq.getFeatured(), ProductSpecification::isFeatured)
                .addIfNotNull(filterReq.getCategoryId(), ProductSpecification::hasCategoryId)
                .add(ProductSpecification.hasPriceBetween(filterReq.getMinPrice(), filterReq.getMaxPrice()))
                .add(ProductSpecification.hasRatingBetween(filterReq.getMinRating(), filterReq.getMaxRating()))
                .build();
            return getMany(spec, pageNo, pageSize, sortDir, sortBy);
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
        if (productUpdatingReq.getFeatured() != null) {
            product.setFeatured(productUpdatingReq.getFeatured());
        }
        if(productUpdatingReq.getCategoryId() != null) {
            Category category = categoryRepository.findById(productUpdatingReq.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);
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
