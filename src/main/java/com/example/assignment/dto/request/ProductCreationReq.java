package com.example.assignment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCreationReq {
    @NotNull(message = "Product name cannot be null")
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    private String description;
    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
    private String imageUrl;
    private Boolean isActive;
    private Boolean featured;

    // Add any other fields as needed
}
