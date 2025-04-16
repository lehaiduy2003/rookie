package com.example.assignment.service.impl;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.ProductDtoRes;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDtoRes createProduct(ProductCreationReq productCreationReq) {
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
    public ProductDtoRes getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return productMapper.toDto(product);
    }


    @Override
    public ProductDtoRes updateProductById(Long id, ProductUpdatingReq productUpdatingReq) {
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
    public ProductDtoRes updateProductCategoryById(Long id, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setCategory(category);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public PagingResult<ProductDtoRes> getProducts(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Product> products = productRepository.findAll(paging);

        if(products.hasContent()) {
            return productMapper.toPagingResult(products, productMapper::toDto);
        } else {
            throw new IllegalArgumentException("No products found");
        }
    }

    @Override
    public PagingResult<ProductDtoRes> getProductsByCategoryId(Long categoryId, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Product> products = productRepository.findByCategory_Id(categoryId, paging);

        if(products.hasContent()) {
            return productMapper.toPagingResult(products, productMapper::toDto);
        } else {
            throw new IllegalArgumentException("No products found");
        }
    }

    @Override
    public PagingResult<ProductDtoRes> getProductsByName(String name, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, paging);

        if(products.hasContent()) {
            return productMapper.toPagingResult(products, productMapper::toDto);
        } else {
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
