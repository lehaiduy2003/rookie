package com.example.assignment.mapper;

import com.example.assignment.dto.response.PagingResult;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.function.Function;

/**
 * Mapper interface for converting between Page<T> and PagingResult<K>.
 * This interface provides a method to convert a Page<T> to a PagingResult<K> using a mapper function.
 * Should extend this interface to convert a Page<T> to a PagingResult<K>.
 */
@Mapper(componentModel = "spring")
public interface PagingResultMapper {

    /**
     * Converts a Page<T> to a PagingResult<K> using a mapper function.
     *
     * @param pages  the page of entities
     * @param mapper function to map each entity to DTO
     * @return the PagingResult of DTOs
     */
    default <T, K> PagingResult<K> toPagingResult(Page<T> pages, Function<T, K> mapper) {
        Collection<K> dtoList = pages.getContent().stream()
            .map(mapper)
            .toList();

        return new PagingResult<>(
            dtoList,
            pages.getTotalPages(),
            pages.getTotalElements(),
            pages.getSize(),
            pages.getNumber(),
            pages.isEmpty()
        );
    }
}
