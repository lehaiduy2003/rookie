package com.example.assignment.mapper;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Product;
import com.example.assignment.mapper.helper.CategoryMappingHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between Product entity and Product DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper(componentModel = "spring", uses = {CategoryMappingHelper.class, UserMapper.class})
public interface ProductMapper extends PagingMapper {
    /**
     * Maps a Product entity to a Product DTO.
     *
     * @param product the Product entity to map
     * @return the mapped Product DTO
     */
    ProductRes toDto(Product product);
    /**
     * Maps a Product entity to a Product detail DTO.
     *
     * @param product the Product entity to map
     * @return the mapped Product detail DTO
     */
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "ratings", target = "ratings")
    ProductDetailRes toDetailsDto(Product product);
    /**
     * Maps a productCreationReq DTO to a Product entity.
     * This method uses a custom mapping for the category field. <br/>
     * This mapper relies on the {@link CategoryMappingHelper} for mapping category-related fields.
     *
     * @param productCreationReq the productCreationReq DTO to map
     * @return the mapped Product entity
     */
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "mapCategoryFromId")
    Product toEntity(ProductCreationReq productCreationReq);

}
