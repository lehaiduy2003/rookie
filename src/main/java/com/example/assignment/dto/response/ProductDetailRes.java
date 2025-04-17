package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class ProductDetailRes {
    private Long id;
    private String name;
    private String description;
    private CategoryRes category;
    private Double price;
    private Integer quantity;
    private double avgRating;
    private long ratingCount;
    private String imageUrl;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
    private Set<RatingRes> ratings;
    private UserRes createdBy;
}
