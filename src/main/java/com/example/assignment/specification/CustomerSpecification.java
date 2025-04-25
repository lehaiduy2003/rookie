package com.example.assignment.specification;

import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import org.springframework.data.jpa.domain.Specification;

/**
 * This class is a placeholder for customer-related specifications.
 * It can be used to define criteria for querying customer data.
 */
public class CustomerSpecification {
    private CustomerSpecification() {
        // Private constructor to prevent instantiation
    }
    /**
     * Specification to filter customers by name.
     * @param name the name to filter by
     * @return a Specification that filters customers by name
     */
    public static Specification<Customer> hasName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    /**
     * Specification to filter customers by member tier.
     * @param tier the member tier to filter by
     * @return a Specification that filters customers by member tier
     */
    public static Specification<Customer> hasMemberTier(MemberTier tier) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("memberTier"), tier);
    }
}
