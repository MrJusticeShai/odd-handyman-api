package com.handyman.oddhandyman.auth.repository;

import com.handyman.oddhandyman.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard JPA operations (save, findById, delete, etc.)
 * and declares custom query methods specific to the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to search for
     * @return an {@link Optional} containing the {@link User} if found, or empty if no user exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user exists with the given email address.
     *
     * @param email the email to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
}
