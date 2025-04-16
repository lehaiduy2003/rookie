package com.example.assignment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCreationReq {
    private String name;
    private String description;
    @NotNull
    @NotBlank(message = "Category ID cannot be null or blank")
    private Long categoryId;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    private Integer quantity;
    private String imageUrl;
    private Boolean isActive;

    // Add any other fields as needed
}
