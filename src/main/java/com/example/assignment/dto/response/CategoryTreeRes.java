package com.example.assignment.dto.response;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CategoryTreeRes {
    private Long id;
    private String name;
    private String description;
    @JsonManagedReference // Prevents infinite recursion during serialization
    private List<CategoryTreeRes> subCategories;
}
