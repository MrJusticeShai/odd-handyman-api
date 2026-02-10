package com.handyman.oddhandyman.profile.repository;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Profile} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard JPA operations (save, findById, delete, etc.)
 * and declares custom query methods specific to profiles.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Retrieves all profiles that are marked as available.
     *
     * @return a list of {@link Profile} entities that are available
     */
    List<Profile> findByAvailableTrue();

    /**
     * Retrieves all available profiles that have a specific skill.
     *
     * @param skill the skill to filter by (case-insensitive)
     * @return a list of {@link Profile} entities matching the skill
     */
    List<Profile> findByAvailableTrueAndSkillsContainingIgnoreCase(String skill);

    /**
     * Retrieves all available profiles where the user's name contains the specified string.
     *
     * @param name partial or full name of the user (case-insensitive)
     * @return a list of {@link Profile} entities matching the name
     */
    List<Profile> findByAvailableTrueAndUserNameContainingIgnoreCase(String name);

    /**
     * Retrieves all available profiles where the user's email contains the specified string.
     *
     * @param email partial or full email of the user (case-insensitive)
     * @return a list of {@link Profile} entities matching the email
     */
    List<Profile> findByAvailableTrueAndUserEmailContainingIgnoreCase(String email);

    /**
     * Retrieves a profile associated with a specific {@link User}.
     *
     * @param user the user whose profile should be retrieved
     * @return an {@link Optional} containing the profile if found
     */
    Optional<Profile> findByUser(User user);

    /**
     * Retrieves a profile by the user's ID.
     *
     * @param userId the ID of the user
     * @return an {@link Optional} containing the profile if found
     */
    Optional<Profile> findByUserId(Long userId);
}
