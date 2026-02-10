package com.handyman.oddhandyman.profile.controller;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    // Update profile (skills, location, phone, availability)
    @PatchMapping("/update")
    public Profile updateProfile(Authentication authentication, @RequestBody Profile updatedProfile) {
        User user = userService.findByEmail(authentication.getName());
        Profile profile = profileService.getProfileByUser(user);

        if (updatedProfile.getSkills() != null) profile.setSkills(updatedProfile.getSkills());
        if (updatedProfile.getLocation() != null) profile.setLocation(updatedProfile.getLocation());
        if (updatedProfile.getPhoneNumber() != null) profile.setPhoneNumber(updatedProfile.getPhoneNumber());
        profile.setAvailable(updatedProfile.isAvailable());

        return profileService.updateProfile(profile);
    }

    // Search available handymen
    @GetMapping("/search")
    public List<Profile> searchHandymen(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        return profileService.searchAvailableHandymen(skill, name, email);
    }

    // Get profile for authenticated user
    @GetMapping("/me")
    public Profile getProfile(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return profileService.getProfileByUser(user);
    }

    @PostMapping("/documents")
    public Profile uploadDocument(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type
    ) throws IOException {
        User user = userService.findByEmail(authentication.getName());
        return profileService.uploadDocument(user, file, type);
    }

    @GetMapping("/documents")
    public Map<String, String> getMyDocuments(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return profileService.getMyDocuments(user);
    }

    @GetMapping("/documents/{type}")
    public String getMyDocumentByType(
            Authentication authentication,
            @PathVariable String type
    ) {
        User user = userService.findByEmail(authentication.getName());
        return profileService.getMyDocumentByType(user, type);
    }

    @GetMapping("/admin/{userId}/documents")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> getUserDocuments(@PathVariable Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return profile.getDocuments();
    }


    @PutMapping("/admin/{userId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public Profile verifyProfile(
            @PathVariable Long userId,
            @RequestParam boolean verified
    ) {
        return profileService.setVerificationStatus(userId, verified);
    }

}
