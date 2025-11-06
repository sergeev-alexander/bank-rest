package com.example.bankcards.service;

import com.example.bankcards.dto.ChangePasswordRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * Service for managing users.
 * Provides functionality for user creation, authentication, and profile management.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Finds a user by identifier.
     *
     * @param id user identifier
     * @return found user
     * @throws com.example.bankcards.exception.NotFoundException if user not found
     */
    User findById(Long id);

    /**
     * Finds a user by email address.
     *
     * @param email user email
     * @return found user
     * @throws com.example.bankcards.exception.NotFoundException if user not found
     */
    User findByEmail(String email);

    /**
     * Gets all users with filtering.
     *
     * @param email email for filtering (can be null)
     * @param role user role for filtering (can be null)
     * @param startDate registration start date (can be null)
     * @param endDate registration end date (can be null)
     * @param pageable pagination parameters
     * @return page of users
     */
    Page<User> getAllUsers(@Nullable String email,
                           @Nullable Role role,
                           @Nullable LocalDateTime startDate,
                           @Nullable LocalDateTime endDate,
                           Pageable pageable);

    /**
     * Creates a new user.
     *
     * @param request user creation request
     * @return created user
     * @throws IllegalArgumentException if user with this email already exists
     */
    User createUser(CreateUserRequest request);

    /**
     * Deletes a user by identifier.
     *
     * @param id user identifier
     * @throws com.example.bankcards.exception.NotFoundException if user not found
     */
    void deleteUserById(Long id);

    /**
     * Changes user password.
     *
     * @param userId user identifier
     * @param request password change request
     * @throws com.example.bankcards.exception.NotFoundException if user not found
     * @throws IllegalArgumentException if old password is incorrect
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * Checks if user exists by email.
     *
     * @param email user email
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if user exists by identifier.
     *
     * @param id user identifier
     * @return true if user exists, false otherwise
     */
    boolean existsById(Long id);
}