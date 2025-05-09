package com.example.assignment.mapper;

import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.response.CategoryRes;
import com.example.assignment.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Category entity and DTOs.
 * This mapper is used to convert CategoryCreationReq to Category entity and Category entity to CategoryRes.
 * The component model is set to "spring" to allow for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    /**
     * Converts a CategoryCreationReq DTO to a Category entity.
     * @param categoryCreationReq the CategoryCreationReq DTO
     * @return the Category entity
     */
    Category toEntity(CategoryCreationReq categoryCreationReq);
    /**
     * Converts a Category entity to a CategoryRes DTO.
     * @param category the Category entity
     * @return the CategoryRes DTO
     */
    @Mapping(target = "parentId", source = "parent.id")
    CategoryRes toDtoRes(Category category);

}
