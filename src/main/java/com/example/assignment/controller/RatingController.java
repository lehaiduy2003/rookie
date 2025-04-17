package com.example.assignment.controller;

import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.request.RatingUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingRes> createRating(@Valid @RequestBody RatingCreationReq ratingCreationReq) {
        RatingRes createdRating = ratingService.createRating(ratingCreationReq);
        return ResponseEntity.status(201).body(createdRating);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingRes> getRatingById(@PathVariable Long id) {
        try {
            RatingRes rating = ratingService.getRatingById(id);
            return ResponseEntity.ok(rating);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<PagingRes<RatingRes>> getAllRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PagingRes<RatingRes> ratings = ratingService.getAllRatings(page, size, sortDir, sortBy);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PagingRes<RatingRes>> getRatingsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PagingRes<RatingRes> ratings = ratingService.getRatingsByProductId(productId, page, size, sortDir, sortBy);
            return ResponseEntity.ok(ratings);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PagingRes<RatingRes>> getRatingsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PagingRes<RatingRes> ratings = ratingService.getRatingsByCustomerId(customerId, page, size, sortDir, sortBy);
            return ResponseEntity.ok(ratings);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingRes> updateRating(@PathVariable Long id, @Valid @RequestBody RatingUpdatingReq ratingUpdatingReq) {
        try {
            RatingRes updatedRating = ratingService.updateRating(id, ratingUpdatingReq);
            return ResponseEntity.ok(updatedRating);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        try {
            ratingService.deleteRating(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
