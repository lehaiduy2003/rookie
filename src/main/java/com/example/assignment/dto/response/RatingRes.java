package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RatingRes {
    private Long id;
    private double score;
    private String comment;
    private Date createdOn;
    private Date updatedOn;
    private Long productId;
    private UserRes customer;
}
