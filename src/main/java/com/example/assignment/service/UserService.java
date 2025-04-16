package com.example.assignment.service;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.UserDtoRes;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * UserService interface for managing user (CRUD) operations.
 * Extends UserDetailsService to provide user details for authentication.
 * This interface defines methods for creating, updating, and deleting users.
 */
public interface UserService extends UserDetailsService {

    /**
     * Creates a new user with UserCreationReq.
     *
     * @param userCreationReq the request object containing user creation details
     * @return the created user
     */
    UserDtoRes createUser(UserCreationReq userCreationReq);

    /**
     * Updates an existing user.
     *
     * @param id the id of the user to update
     * @param userInfoUpdatingReq the request object containing user update details
     * @return the updated user
     */
    UserDtoRes updateUserById(Long id, UserInfoUpdatingReq userInfoUpdatingReq);

    /**
     * Retrieves a user by id.
     *
     * @param id the id of the user to retrieve
     * @return the retrieved user
     */
    UserDtoRes getUserById(Long id);

    /**
     * Deletes a user by id.
     *
     * @param id the id of the user to delete
     */
    void deleteUserById(Long id);

    /**
     * Retrieves a user using pagination.
     *
     * @param pageNo the page number to retrieve
     * @param pageSize the number of users per page
     * @param sortBy the field to sort by
     * @return the retrieved paging result of users
     */
    PagingResult<UserDtoRes> getUsers(Integer pageNo, Integer pageSize, String sortBy);
}
