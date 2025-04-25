package com.example.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;


/**
 * This interface extends JpaSpecificationExecutor to provide
 * specification-based querying capabilities for entities.
 * @param <E> the entity type
 */
@NoRepositoryBean
public interface BaseRepository<E, K extends Serializable> extends JpaRepository<E, K>, JpaSpecificationExecutor<E> {
}
