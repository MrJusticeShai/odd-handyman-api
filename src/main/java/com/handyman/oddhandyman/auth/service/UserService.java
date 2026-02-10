package com.handyman.oddhandyman.auth.service;

import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.exception.UserConflictException;

/**
 * Service interface for user-related operations, including registration and retrieval.
 * This service handles the business logic for creating users and querying them by email.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param req the registration request containing user details such as name, email, password, and role
     * @return the created User entity
     * @throws UserConflictException if a user with the given email already exists
     */
    User register(RegisterRequest req);

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return the User entity matching the email, or null if no user exists
     */
    User findByEmail(String email);
}
