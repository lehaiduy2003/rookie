package com.example.assignment.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryFilterReq {
    private String name;
    private Long parentId;
    private String description;
}
