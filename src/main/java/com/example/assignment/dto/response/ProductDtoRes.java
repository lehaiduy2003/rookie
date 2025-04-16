package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductDtoRes {
    private Long id;
    private String name;
    private String description;
    private CategoryDtoRes category;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
}
