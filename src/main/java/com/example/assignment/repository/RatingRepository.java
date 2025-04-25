package com.example.assignment.repository;

import com.example.assignment.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingRepository extends BaseRepository<Rating, Long> {
    Page<Rating> findByProductId(Long productId, Pageable pageable);
    Page<Rating> findByCustomerId(Long customerId, Pageable pageable);
}
