package com.example.assignment.mapper;

import com.example.assignment.dto.response.PagingRes;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.function.Function;

/**
 * Mapper interface for converting between Page<T> and PagingRes<K>.
 * This interface provides a method to convert a Page<T> to a PagingRes<K> using a mapper function.
 * Should extend this interface to convert a Page<T> to a PagingRes<K>.
 */
@Mapper(componentModel = "spring")
public interface PagingMapper {

    /**
     * Converts a Page<T> to a PagingRes<K> using a mapper function.
     *
     * @param pages  the page of entities
     * @param mapper function to map each entity to DTO
     * @return the PagingRes of DTOs
     */
    default <T, K> PagingRes<K> toPagingResult(Page<T> pages, Function<T, K> mapper) {
        Collection<K> dtoList = pages.getContent().stream()
            .map(mapper)
            .toList();

        return new PagingRes<>(
            dtoList,
            pages.getTotalPages(),
            pages.getTotalElements(),
            pages.getSize(),
            pages.getNumber(),
            pages.isEmpty()
        );
    }
}
