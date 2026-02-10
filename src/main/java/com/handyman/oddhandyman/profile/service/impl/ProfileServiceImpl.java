package com.handyman.oddhandyman.profile.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.exception.ProfileNotFoundException;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.repository.ProfileRepository;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile createProfileForUser(User user) {

        // Check if profile already exists for this user
        return profileRepository.findByUser(user)
                .orElseGet(() -> {

                    Profile profile = new Profile();
                    profile.setUser(user);
                    profile.setVerified(false);
                    profile.setAvailable(false);
                    profile.setCreatedAt(LocalDateTime.now());
                    profile.setSkills(new ArrayList<>());  // prevent null issues
                    profile.setRating(0.0);

                    if (user.getRole() == Role.HANDYMAN) {
                        profile.setAvailable(true);
                    }

                    return profileRepository.save(profile);
                });
    }


    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }

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

    public Profile getProfileByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException("Profile Not Found"));
    }

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile Not Found"));
    }

    public Profile setVerificationStatus(Long userId, boolean verified) {
        Profile profile = getProfileByUserId(userId);
        profile.setVerified(verified);
        return profileRepository.save(profile);
    }

}