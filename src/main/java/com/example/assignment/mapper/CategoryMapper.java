package com.example.assignment.mapper;

import com.example.assignment.dto.request.CategoryDtoReq;
import com.example.assignment.dto.response.CategoryDtoRes;
import com.example.assignment.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Category entity and DTOs.
 * This mapper is used to convert CategoryDtoReq to Category entity and Category entity to CategoryDtoRes.
 * Component model is set to "spring" to allow for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    /**
     * Converts a CategoryDtoReq DTO to a Category entity.
     * @param categoryDtoReq the CategoryDtoReq DTO
     * @return the Category entity
     */
    Category toEntity(CategoryDtoReq categoryDtoReq);
    /**
     * Converts a Category entity to a CategoryDtoRes DTO.
     * @param category the Category entity
     * @return the CategoryDtoRes DTO
     */
    @Mapping(target = "parentId", source = "parent.id")
    CategoryDtoRes toDtoRes(Category category);

}
