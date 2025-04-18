package com.example.assignment.repository;

import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Customer entities.
 * This interface extends JpaRepository to provide CRUD operations.
 * It includes a method for finding customers by their member tier.
 * The method uses pagination to return a page of customers.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * Finds a page of customers by their member tier.
     *
     * @param memberTier the member tier to filter customers by
     * @param pageable   the pagination information
     * @return a page of customers belonging to the specified member tier
     */
    Page<Customer> findByMemberTier(MemberTier memberTier, Pageable pageable);
}
