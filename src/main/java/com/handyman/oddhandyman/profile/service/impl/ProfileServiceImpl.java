package com.handyman.oddhandyman.profile.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.exception.ProfileNotFoundException;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.repository.ProfileRepository;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ProfileService} for managing handyman and customer profiles.
 * <p>
 * Handles profile creation, updating, searching, retrieval, and verification.
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Creates a profile for the given user if one does not already exist.
     * <p>
     * Handyman profiles are automatically set as available, while other roles are not.
     *
     * @param user the user to create a profile for
     * @return the existing or newly created {@link Profile}
     */
    @Override
    public Profile createProfileForUser(User user) {
        return profileRepository.findByUser(user)
                .orElseGet(() -> {
                    Profile profile = new Profile();
                    profile.setUser(user);
                    profile.setVerified(false);
                    profile.setAvailable(user.getRole() == Role.HANDYMAN);
                    profile.setCreatedAt(LocalDateTime.now());
                    profile.setSkills(new ArrayList<>()); // prevent null issues
                    profile.setRating(0.0);
                    return profileRepository.save(profile);
                });
    }

    /**
     * Updates an existing profile in the database.
     *
     * @param profile the profile with updated information
     * @return the updated {@link Profile}
     */
    @Override
    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    /**
     * Searches for available handymen, filtered by skill, name, or email if provided.
     *
     * @param skill optional skill filter
     * @param name optional name filter
     * @param email optional email filter
     * @return a list of {@link Profile} entities that match the search criteria
     */
    @Override
    public List<Profile> searchAvailableHandymen(String skill, String name, String email) {
        if (skill != null && !skill.isEmpty()) {
            return profileRepository.findByAvailableTrueAndSkillsContainingIgnoreCase(skill);
        } else if (name != null && !name.isEmpty()) {
            return profileRepository.findByAvailableTrueAndUserNameContainingIgnoreCase(name);
        } else if (email != null && !email.isEmpty()) {
            return profileRepository.findByAvailableTrueAndUserEmailContainingIgnoreCase(email);
        } else {
            return profileRepository.findByAvailableTrue();
        }
    }

    /**
     * Retrieves the profile for a given user.
     *
     * @param user the user whose profile is being retrieved
     * @return the corresponding {@link Profile}
     * @throws ProfileNotFoundException if no profile exists for the user
     */
    @Override
    public Profile getProfileByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException("Profile Not Found"));
    }

    /**
     * Retrieves the profile for a given user ID.
     *
     * @param userId the ID of the user
     * @return the corresponding {@link Profile}
     * @throws ProfileNotFoundException if no profile exists for the user
     */
    @Override
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile Not Found"));
    }

    /**
     * Sets the verification status of a user's profile.
     *
     * @param userId the ID of the user whose profile is to be verified/unverified
     * @param verified the new verification status
     * @return the updated {@link Profile}
     */
    @Override
    public Profile setVerificationStatus(Long userId, boolean verified) {
        Profile profile = getProfileByUserId(userId);
        profile.setVerified(verified);
        return profileRepository.save(profile);
    }
}
