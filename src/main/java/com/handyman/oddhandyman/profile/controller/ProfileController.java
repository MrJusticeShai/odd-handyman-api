package com.handyman.oddhandyman.profile.controller;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing user profiles, including updating profile information,
 * searching handymen, and performing administrative profile actions.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    /**
     * Updates the authenticated user's profile.
     *
     * @param authentication  the current authenticated user
     * @param updatedProfile  partial or full profile data to update
     * @return the updated {@link Profile}
     */
    @PatchMapping("/update")
    public Profile updateProfile(
            @AuthenticationPrincipal UserDetails authentication,
            @RequestBody Profile updatedProfile
    ) {
        User user = userService.findByEmail(authentication.getUsername());
        Profile profile = profileService.getProfileByUser(user);

        if (updatedProfile.getSkills() != null) profile.setSkills(updatedProfile.getSkills());
        if (updatedProfile.getLocation() != null) profile.setLocation(updatedProfile.getLocation());
        if (updatedProfile.getPhoneNumber() != null) profile.setPhoneNumber(updatedProfile.getPhoneNumber());
        profile.setAvailable(updatedProfile.isAvailable());

        return profileService.updateProfile(profile);
    }

    /**
     * Searches available handymen by optional skill, name, or email.
     *
     * @param skill optional skill to filter by
     * @param name  optional name to filter by
     * @param email optional email to filter by
     * @return a list of matching {@link Profile} entities
     */
    @GetMapping("/search")
    public List<Profile> searchHandymen(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        return profileService.searchAvailableHandymen(skill, name, email);
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * @param authentication the current authenticated user
     * @return the {@link Profile} of the authenticated user
     */
    @GetMapping("/me")
    public Profile getProfile(
            @AuthenticationPrincipal UserDetails authentication
    ) {
        User user = userService.findByEmail(authentication.getUsername());
        return profileService.getProfileByUser(user);
    }

    /**
     * Retrieves all documents associated with a user's profile. Admin-only endpoint.
     *
     * @param userId ID of the user
     * @return a map of document names to URLs or file paths
     */
    @GetMapping("/admin/{userId}/documents")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> getUserDocuments(@PathVariable Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return profile.getDocuments();
    }

    /**
     * Sets the verification status of a user's profile. Admin-only endpoint.
     *
     * @param userId   ID of the user
     * @param verified true to verify, false to unverify
     * @return the updated {@link Profile}
     */
    @PutMapping("/admin/{userId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public Profile verifyProfile(
            @PathVariable Long userId,
            @RequestParam boolean verified
    ) {
        return profileService.setVerificationStatus(userId, verified);
    }
}
