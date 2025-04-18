package com.example.assignment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductUpdatingReq {
    @NotBlank(message = "Product name cannot be blank if provided")
    private String name;
    private String description;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
    private String imageUrl;
    private Boolean isActive;
}
