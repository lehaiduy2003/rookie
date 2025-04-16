package com.example.assignment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDtoReq {
    private String name;
    private String description;
    private Long parentId;
}
