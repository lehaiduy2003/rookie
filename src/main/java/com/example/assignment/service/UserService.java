package com.example.assignment.service;
import com.example.assignment.dto.request.*;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;


/**
 * UserService interface for managing user (CRUD) operations.
 * Extends UserDetailsService to provide user details for authentication.
 * This interface defines methods for creating, updating, and deleting users.
 */
public interface UserService {

    /**
     * Find a user by email.
     * This method retrieves a user by their email address.
     * It uses for Spring Security to load user-specific data for authentication.
     * This method is used by {@link AuthService}
     * @param email The email of the user to retrieve
     * @return The User entity
     * @throws UsernameNotFoundException if the user is not found
     */
    User findByEmail(String email);

    /**
     * Creates a new user with UserCreationReq.
     *
     * @param userCreationReq the request object containing user creation details
     * @return the created user
     */
    UserDetailsRes createUser(UserCreationReq userCreationReq);

    /**
     * Updates an existing user.
     *
     * @param id the id of the user to update
     * @param userInfoUpdatingReq the request object containing user update details
     * @return the updated user
     */
    UserDetailsRes updateUserById(Long id, UserInfoUpdatingReq userInfoUpdatingReq);


    /**
     * Updates an existing user.
     * This method is used for updating user information by admin.
     *
     * @param id the id of the user to update
     * @param userUpdatingReq the request object containing user update details
     * @return the updated user
     */
    UserDetailsRes updateUserById(Long id, UserUpdatingReq userUpdatingReq);



    /**
     * Retrieves a user by id.
     *
     * @param id the id of the user to retrieve
     * @return the retrieved user
     */
    UserDetailsRes getUserById(Long id);

    /**
     * Deletes a user by id.
     *
     * @param id the id of the user to delete
     */
    void deleteUserById(Long id);

    /**
     * Retrieves a user using pagination.
     * @param filter the filter criteria for retrieving users
     * @param pageNo the page number to retrieve
     * @param pageSize the number of users per page
     * @param sortDir the direction to sort (ascending or descending)
     * @param sortBy the field to sort by
     * @return the retrieved paging result of users
     */
    PagingRes<UserDetailsRes> getUsers(UserFilterReq filter, Integer pageNo, Integer pageSize, String sortDir, String sortBy);

    /**
     * Checks if a user exists by email.
     *
     * @param email the email of the user to check
     * @return true if the user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Bulk deletes a list of customers.
     * @param ids the list of customer IDs to delete
     */
    void bulkDeleteCustomers(List<Long> ids);
}
