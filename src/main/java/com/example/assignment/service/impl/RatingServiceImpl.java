package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.request.RatingUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.Rating;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.RatingMapper;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.repository.RatingRepository;
import com.example.assignment.service.RatingService;
import com.example.assignment.service.impl.paging.RatingPagingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Logging
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final RatingMapper ratingMapper;
    private final RatingPagingServiceImpl ratingPagingService;

    @Override
    @Transactional
    public RatingRes createRating(RatingCreationReq ratingCreationReq) {
        Long productId = ratingCreationReq.getProductId();
        Long customerId = ratingCreationReq.getCustomerId();
        if (productId == null || customerId == null) {
            throw new IllegalArgumentException("Product ID and Customer ID cannot be null");
        }
        double score = ratingCreationReq.getScore();
        // Fetch customer and product to ensure they exist
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // start creating the rating
        Rating rating = ratingMapper.toEntity(ratingCreationReq);
        rating.setProduct(product);
        rating.setCustomer(customer);
        Rating savedRating = ratingRepository.save(rating);
        // Update product rating
        productRepository.updateProductRating(productId, score);
        return ratingMapper.toDto(savedRating);
    }

    @Override
    public RatingRes getRatingById(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        return ratingMapper.toDto(rating);
    }

    @Override
    public PagingRes<RatingRes> getAllRatings(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        return ratingPagingService.getMany(pageNo, pageSize, sortDir, sortBy);
    }

    @Override
    public PagingRes<RatingRes> getRatingsByProductId(Long productId, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        // Verify the product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        Pageable pageable = ratingPagingService.createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<Rating> ratingPage = ratingPagingService.findByProductId(productId, pageable);
        return ratingPagingService.toPagingResult(ratingPage, ratingPagingService::convertToDto);
    }

    @Override
    public PagingRes<RatingRes> getRatingsByCustomerId(Long customerId, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        // Verify customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        Pageable pageable = ratingPagingService.createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<Rating> ratingPage = ratingPagingService.findByCustomerId(customerId, pageable);
        return ratingPagingService.toPagingResult(ratingPage, ratingPagingService::convertToDto);
    }

    @Override
    @Transactional
    public RatingRes updateRating(Long id, RatingUpdatingReq ratingUpdatingReq) {
        Rating existingRating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));

        // Verify the customer ID matches the original rating's customer
        if (!existingRating.getCustomer().getId().equals(ratingUpdatingReq.getCustomerId())) {
            throw new IllegalArgumentException("Rating can only be updated by the same customer who created it");
        }

        // Only update the comment field
        existingRating.setComment(ratingUpdatingReq.getComment());

        // Product cannot be changed for an existing rating
        // Score is not updated as it's not part of RatingUpdatingReq

        Rating updatedRating = ratingRepository.save(existingRating);
        return ratingMapper.toDto(updatedRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long id) {
        if (!ratingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rating not found with id: " + id);
        }
        ratingRepository.deleteById(id);
    }
}
