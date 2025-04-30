package com.example.assignment.dto.response;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class ProductDetailRes extends ProductRes {
    private String description;
    private CategoryRes category;
    private Integer quantity;
    private Date createdOn;
    private Date updatedOn;
    private Set<RatingRes> ratings;
    private UserRes createdBy;

    @Builder
    public ProductDetailRes(Long id, String name, Double price, String imageUrl, Boolean isActive, Double avgRating, Long ratingCount, String description, CategoryRes category, Integer quantity, Date createdOn, Date updatedOn, Set<RatingRes> ratings, UserRes createdBy) {
        super(id, name, price, imageUrl, isActive, avgRating, ratingCount);
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.ratings = ratings;
        this.createdBy = createdBy;
    }
}
