package com.handyman.oddhandyman.profile.service;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.profile.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for managing {@link Profile} entities.
 * <p>
 * Provides methods for creating, updating, retrieving, searching, and verifying profiles.
 */
public interface ProfileService {

    /**
     * Creates a new profile for a given user.
     *
     * @param user the user for whom the profile will be created
     * @return the newly created {@link Profile}
     */
    Profile createProfileForUser(User user);

    /**
     * Updates an existing profile with new information.
     *
     * @param profile the profile containing updated fields
     * @return the updated {@link Profile}
     */
    Profile updateProfile(Profile profile);

    /**
     * Searches for available handymen based on optional criteria.
     *
     * @param skill skill to filter by (optional)
     * @param name name to filter by (optional)
     * @param email email to filter by (optional)
     * @return a list of matching {@link Profile} entities
     */
    List<Profile> searchAvailableHandymen(String skill, String name, String email);

    /**
     * Retrieves the profile associated with a given user.
     *
     * @param user the user whose profile should be retrieved
     * @return the corresponding {@link Profile}
     */
    Profile getProfileByUser(User user);

    /**
     * Retrieves the profile associated with a given user ID.
     *
     * @param userId the ID of the user
     * @return the corresponding {@link Profile}
     */
    Profile getProfileByUserId(Long userId);

    /**
     * Updates the verification status of a user's profile.
     *
     * @param userId the ID of the user
     * @param verified the new verification status (true if verified)
     * @return the updated {@link Profile}
     */
    Profile setVerificationStatus(Long userId, boolean verified);
}
