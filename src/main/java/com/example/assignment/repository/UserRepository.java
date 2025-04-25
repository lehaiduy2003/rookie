package com.example.assignment.repository;

import com.example.assignment.entity.User;
import jakarta.validation.constraints.Email;

/**
 * Repository interface for managing User entities.
 * It extends JpaRepository to provide CRUD operations.
 * Contains some custom methods.
 */
public interface UserRepository extends BaseRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return the user with the specified email address, or null if no such user exists
     */
    User findByEmail(String email);
    /**
     * Checks if a user with the specified email address exists.
     *
     * @param email the email address to check
     * @return true if a user with the specified email address exists, false otherwise
     */
    boolean existsUserByEmail(@Email String email);
}
