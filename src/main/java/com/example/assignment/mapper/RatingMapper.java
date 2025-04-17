package com.example.assignment.mapper;

import com.example.assignment.dto.request.RatingCreationReq;
import com.example.assignment.dto.response.RatingRes;
import com.example.assignment.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Rating entity and DTOs.
 * This mapper is used to convert RatingCreationReq to Rating entity and Rating entity to RatingRes.
 * It extends PagingMapper to provide pagination support.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface RatingMapper extends PagingMapper {
    /**
     * Converts a RatingCreationReq DTO to a Rating entity.
     * @param ratingCreationReq the RatingCreationReq DTO
     * @return the Rating entity
     */
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    Rating toEntity(RatingCreationReq ratingCreationReq);

    /**
     * Converts a Rating entity to a RatingRes DTO.
     * @param rating the Rating entity
     * @return ratingRes DTO
     */
    @Mapping(target = "productId", source = "product.id")
    RatingRes toDto(Rating rating);
}
