package com.handyman.oddhandyman.auth.service.impl;

import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.exception.UserConflictException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService that handles user registration and retrieval.
 *
 * This service handles all business logic related to creating users, encoding passwords,
 * assigning roles, and retrieving users by email.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system.
     *
     * Steps performed:
     * 1. Checks if a user with the given email already exists.
     * 2. Throws {@link UserConflictException} if the email is already taken.
     * 3. Encodes the user's password using {@link PasswordEncoder}.
     * 4. Converts the role string from the request to {@link Role} enum.
     * 5. Saves the new user to the database.
     *
     * @param req the registration request containing name, email, password, and role
     * @return the saved {@link User} entity
     * @throws UserConflictException if a user with the same email already exists
     * @throws IllegalArgumentException if the role in the request does not match any {@link Role} enum
     */
    @Transactional
    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserConflictException("User Already Exists");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.valueOf(req.getRole()));

        return userRepository.save(u);
    }

    /**
     * Finds a user by email address.
     *
     * @param email the email of the user to retrieve
     * @return the {@link User} entity matching the email
     * @throws UserNotFoundException if no user is found with the given email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }
}
