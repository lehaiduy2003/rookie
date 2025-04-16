package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CategoryDtoRes {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
}
