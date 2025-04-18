package com.example.assignment.repository;

import com.example.assignment.entity.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repository interface for managing Product entities.
 * This interface extends JpaRepository to provide CRUD operations.
 * It includes methods for finding products by category ID and name.
 * FindById method is overridden to include category information in the result.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds a page of products by category ID.
     * @param categoryId the ID of the category to filter products by
     * @param pageable the pagination information
     * @return a page of products belonging to the specified category
     */
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    /**
     * Finds a page of products by name, ignoreCase.
     * @param name the name to search for
     * @param pageable the pagination information
     * @return a page of products whose names contain the specified string, ignoring case
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds a page of products by name and category ID.
     * Overrides the default method to include category information in the result.
     * The annotation @EntityGraph is used to fetch the category entity along with the product.
     * @param id the ID of the product to find
     * @return an Optional containing the product if found, or empty if not found
     */
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(@NonNull Long id);

    /**
     * Updates the average rating and rating count of a product.
     * This method is used to update the product's average rating after a new rating is added.
     * It uses a native SQL query to perform the update. So this method does not need to save the product entity.
     * @param productId the ID of the product to update
     * @param score the score to add to the product's average rating
     */
    @Modifying
    @Query(value = """
  UPDATE products\s
  SET average_rating = ((average_rating * rating_count + :score) / (rating_count + 1)),
      rating_count = rating_count + 1
  WHERE id = :productId
""", nativeQuery = true)
    void updateProductRating(@Param("productId") Long productId, @Param("score") double score);

    /**
     * Finds a page of products that are featured.
     * @param featured the featured status to filter products by
     * @param pageable the pagination information
     * @return a page of products that are featured
     */
    Page<Product> findByFeatured(boolean featured, Pageable pageable);
}
