package com.example.assignment.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFilterReq {
    private String name;
    private Long categoryId;
    private Boolean featured;
    private Boolean isActive;
    @DecimalMin(value = "0.0", message = "Min price must be >= 0")
    private Double minPrice;
    @DecimalMin(value = "0.0", message = "Max price must be >= 0")
    private Double maxPrice;
    @DecimalMin(value = "0.0", message = "Min rating must be >= 0")
    private Double minRating;
    @DecimalMin(value = "0.0", message = "Max rating must be >= 0")
    @DecimalMax(value = "5.0", message = "Max rating must be <= 5")
    private Double maxRating;
}
