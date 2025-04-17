package com.example.assignment.service;

import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;

/**
 * This interface defines the contract for customer-related services.
 */
public interface CustomerService {
    /**
     * Update the member tier of a customer.
     * This method has no return value, the controller should return no content response.
     * @param customerId the ID of the customer whose member tier is to be updated
     * @param memberTier the new member tier to be set
     */
    void updateMemberTier(Long customerId, String memberTier);

    /**
     * Get a list of customers by their member tier.
     * @param memberTier the member tier to filter customers by
     * @param pageNo the page number for pagination
     * @param pageSize the number of customers per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a pageable result of customers with the specified member tier
     */
    PagingRes<UserRes> getCustomersByTier(String memberTier, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Get a list of customers with pagination and sorting.
     * @param pageNo the page number for pagination
     * @param pageSize the number of customers per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return a pageable result of customers
     */
    PagingRes<UserRes> getCustomers(Integer pageNo, Integer pageSize, String sortDir, String sortBy);

}
