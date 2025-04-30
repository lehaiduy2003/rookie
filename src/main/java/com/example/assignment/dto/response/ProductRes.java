package com.example.assignment.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRes {
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private Boolean isActive;
    private Double avgRating;
    private Long ratingCount;
}
