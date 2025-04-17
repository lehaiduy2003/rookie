package com.example.assignment.service;

import com.example.assignment.dto.response.PagingRes;

/**
 * PagingService interface for handling pagination and sorting of entities.
 * @param <T> the type of the DTO
 * @param <K> the type of the entity's identifier
 */
public interface PagingService<T, K> {
    /**
     * Get a paginated list of entities.
     * @param pageNo number of the page to retrieve
     * @param pageSize number of items per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a paginated response containing the entities
     */
    PagingRes<T> getMany(Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Get a paginated list of entities by ID.
     * Use to get a paginated list of entities by ID. <br/>
     * Ex1: retrieve a paginated list of products by category ID. <br/>
     * Ex2: retrieve a paginated list of ratings by product ID.
     * @param id the ID of the entity to retrieve
     * @param pageNo number of the page to retrieve
     * @param pageSize number of items per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a paginated response containing the entities
     */
    PagingRes<T> getById(K id, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Get a paginated list of entities by criteria.
     * Use to get a paginated list of entities by criteria. <br/>
     * Ex1: retrieve a paginated list of products by product name. <br/>
     * Ex2: retrieve a paginated list of ratings by rating score.
     * @param criteria the criteria to filter the entities
     * @param pageNo number of the page to retrieve
     * @param pageSize number of items per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a paginated response containing the entities
     */
    PagingRes<T> getByCriteria(String criteria, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

}
