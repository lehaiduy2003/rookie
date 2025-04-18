package com.example.assignment.service.impl.paging;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.service.PagingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.Function;

/**
 * Abstract implementation of PagingService that provides common pagination and sorting functionality.
 * @param <T> the type of the DTO
 * @param <E> the type of the entity
 * @param <K> the type of the entity's identifier
 */
@Logging
public abstract class PagingServiceImpl<T, E, K> implements PagingService<T, K> {

    /**
     * Get the repository for the entity.
     * @return the repository
     */
    protected abstract JpaRepository<E, K> getRepository();

    /**
     * Convert a page of entities to a page of DTOs.
     * @param page the page of entities
     * @param converter the function to convert an entity to a DTO
     * @return a paginated response containing the DTOs
     */
    protected abstract PagingRes<T> toPagingResult(Page<E> page, Function<E, T> converter);

    /**
     * Create a Pageable object with the given parameters.
     * @param pageNo number of the page to retrieve
     * @param pageSize number of items per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a Pageable object
     */
    public Pageable createPageable(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    @Override
    public PagingRes<T> getMany(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        Pageable pageable = createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<E> page = getRepository().findAll(pageable);
        return toPagingResult(page, this::convertToDto);
    }

    @Override
    public PagingRes<T> getById(K id, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        // Check if the entity with the given ID exists
        if (!getRepository().existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id: " + id);
        }

        Pageable pageable = createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<E> page = findByRelatedId(id, pageable);
        return toPagingResult(page, this::convertToDto);
    }

    @Override
    public PagingRes<T> getByCriteria(String criteria, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        Pageable pageable = createPageable(pageNo, pageSize, sortDir, sortBy);
        Page<E> page = findByCriteria(criteria, pageable);
        return toPagingResult(page, this::convertToDto);
    }

    /**
     * Find entities by a related ID.
     * This method should be implemented by subclasses to handle specific repository calls.
     * @param id the ID to filter by
     * @param pageable the pagination information
     * @return a page of entities
     */
    protected abstract Page<E> findByRelatedId(K id, Pageable pageable);

    /**
     * Find entities by criteria.
     * This method should be implemented by subclasses to handle specific repository calls.
     * @param criteria the criteria to filter by
     * @param pageable the pagination information
     * @return a page of entities
     */
    protected abstract Page<E> findByCriteria(String criteria, Pageable pageable);

    /**
     * Convert an entity to a DTO.
     * @param entity the entity to convert
     * @return the DTO
     */
    protected abstract T convertToDto(E entity);
}
