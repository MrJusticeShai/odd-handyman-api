package com.oddhandyman.profile.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.oddhandyman.auth.entity.Role;
import com.oddhandyman.auth.entity.User;
import com.oddhandyman.profile.entity.Profile;
import com.oddhandyman.profile.repository.ProfileRepository;
import com.oddhandyman.profile.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final Cloudinary cloudinary;

    public ProfileServiceImpl(ProfileRepository profileRepository, Cloudinary cloudinary) {
        this.profileRepository = profileRepository;
        this.cloudinary = cloudinary;
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
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile uploadDocument(User user, MultipartFile file, String type) throws IOException {
        // Validate type
        if (!Set.of("identity", "criminal_clearance", "headshot").contains(type)) {
            throw new IllegalArgumentException("Invalid document type");
        }

        // Get profile
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getCloudinaryFolderUuid() == null) {
            profile.setCloudinaryFolderUuid(
                    UUID.randomUUID().toString().replace("-", "")
            );
            profileRepository.save(profile);
        }

        // Use UUID folder
        String folder = "profiles/" + profile.getCloudinaryFolderUuid() + "/" + type;

        // Upload
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"
                ));

        String url = (String) uploadResult.get("secure_url");

        // Store in documents map
        profile.getDocuments().put(type, url);

        // Save and return
        return profileRepository.save(profile);
    }

    public Map<String, String> getMyDocuments(User user) {
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profile.getDocuments();
    }

    public String getMyDocumentByType(User user, String type) {
        if (!List.of("identity", "criminal_clearance", "headshot").contains(type)) {
            throw new IllegalArgumentException("Invalid document type");
        }

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profile.getDocuments().get(type);
    }

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile setVerificationStatus(Long userId, boolean verified) {
        Profile profile = getProfileByUserId(userId);
        profile.setVerified(verified);
        return profileRepository.save(profile);
    }

}