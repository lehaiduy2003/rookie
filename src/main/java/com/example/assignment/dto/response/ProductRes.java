package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRes {
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private Boolean isActive;
    private Double avgRating;
}
