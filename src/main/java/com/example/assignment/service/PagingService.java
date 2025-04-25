package com.example.assignment.service;

import com.example.assignment.dto.response.PagingRes;
import org.springframework.data.jpa.domain.Specification;

/**
 * PagingService interface for handling pagination and sorting of entities.
 * @param <T> the type of the DTO
 * @param <E> the type of the entity
 */
public interface PagingService<T, E> {
    /**
     * Get a paginated list of entities.
     * @param spec the spec to filter the entities. This can be null.
     * @param pageNo number of the page to retrieve
     * @param pageSize number of items per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a paginated response containing the entities
     */
    PagingRes<T> getMany(Specification<E> spec, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

}
