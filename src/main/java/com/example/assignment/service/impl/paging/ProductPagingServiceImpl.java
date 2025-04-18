package com.example.assignment.service.impl.paging;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Product;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Logging
public class ProductPagingServiceImpl extends PagingServiceImpl<ProductRes, Product, Long> {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductPagingServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    protected JpaRepository<Product, Long> getRepository() {
        return productRepository;
    }

    @Override
    protected PagingRes<ProductRes> toPagingResult(Page<Product> page, Function<Product, ProductRes> converter) {
        return productMapper.toPagingResult(page, converter);
    }

    @Override
    protected Page<Product> findByRelatedId(Long id, Pageable pageable) {
        // This method can handle category ID
        return productRepository.findByCategory_Id(id, pageable);
    }

    @Override
    protected Page<Product> findByCriteria(String criteria, Pageable pageable) {
        // This method handles search by name
        return productRepository.findByNameContainingIgnoreCase(criteria, pageable);
    }

    public PagingRes<ProductRes> getFeaturedProducts(boolean featured, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        // This method can be used to get featured products
        Pageable pageable = super.createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<Product> products = productRepository.findByFeatured(featured, pageable);
        return toPagingResult(products, this::convertToDto);
    }

    @Override
    protected ProductRes convertToDto(Product entity) {
        return productMapper.toDto(entity);
    }
}
