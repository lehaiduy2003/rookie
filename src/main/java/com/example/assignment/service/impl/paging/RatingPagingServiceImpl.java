package com.example.assignment.service.impl.paging;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.entity.Rating;
import com.example.assignment.mapper.RatingMapper;
import com.example.assignment.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Logging
public class RatingPagingServiceImpl extends PagingServiceImpl<RatingRes, Rating, Long> {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public RatingPagingServiceImpl(RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }

    @Override
    protected JpaRepository<Rating, Long> getRepository() {
        return ratingRepository;
    }

    @Override
    public PagingRes<RatingRes> toPagingResult(Page<Rating> page, Function<Rating, RatingRes> converter) {
        return ratingMapper.toPagingResult(page, converter);
    }

    @Override
    protected Page<Rating> findByRelatedId(Long id, Pageable pageable) {
        // This method can handle both product ID and customer ID
        // The caller should specify which one to use
        return null;
    }

    public Page<Rating> findByProductId(Long productId, Pageable pageable) {
        return ratingRepository.findByProductId(productId, pageable);
    }

    public Page<Rating> findByCustomerId(Long customerId, Pageable pageable) {
        return ratingRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    protected Page<Rating> findByCriteria(String criteria, Pageable pageable) {
        // Not implemented for ratings
        return Page.empty(pageable);
    }

    @Override
    public RatingRes convertToDto(Rating entity) {
        return ratingMapper.toDto(entity);
    }
}
