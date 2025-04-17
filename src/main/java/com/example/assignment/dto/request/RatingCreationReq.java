package com.example.assignment.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingCreationReq {
    @DecimalMin(value = "1.0", message = "Score must be at least 1")
    @DecimalMax(value = "5.0", message = "Score must be at most 5")
    private double score;
    @Size(max = 500, message = "Comment must be at most 500 characters")
    private String comment;
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;
}
