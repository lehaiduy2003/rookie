package com.example.assignment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingUpdatingReq {
    private String comment;
    private Long customerId;
}
