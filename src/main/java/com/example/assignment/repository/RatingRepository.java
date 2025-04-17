package com.example.assignment.repository;

import com.example.assignment.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByProductId(Long productId, Pageable pageable);
    Page<Rating> findByCustomerId(Long customerId, Pageable pageable);
}
