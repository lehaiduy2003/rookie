package com.example.assignment.specification;

import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * This class is a placeholder for customer-related specifications.
 * It can be used to define criteria for querying customer data.
 */
public class UserSpecification {
    private UserSpecification() {
        // Private constructor to prevent instantiation
    }
    /**
     * Specification to filter customers by name.
     * @param name the name to filter by
     * @return a Specification that filters customers by name
     */
    public static Specification<Customer> hasFirstName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), name);
    }

    public static Specification<Customer> hasLastName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), name);
    }

    /**
     * Specification to filter customers by member tier.
     * @param tier the member tier to filter by
     * @return a Specification that filters customers by member tier
     */
    public static Specification<Customer> hasMemberTier(MemberTier tier) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("memberTier"), tier);
    }

    public static Specification<Customer> hasActiveStatus(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Customer> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
            email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Customer> hasCreatedOn(Date createdOn) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdOn"), createdOn);
    }

    public static Specification<Customer> hasUpdatedOn(Date updatedOn) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("updatedOn"), updatedOn);
    }

    public static Specification<Customer> hasRole(Role role) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role);
    }
}
