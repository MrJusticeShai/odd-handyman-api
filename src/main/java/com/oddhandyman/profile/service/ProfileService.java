package com.oddhandyman.profile.service;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.profile.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public interface ProfileService {

    Profile createProfileForUser(User user);

    Profile updateProfile(Profile profile);

    List<Profile> searchAvailableHandymen(String skill, String name, String email);

    Profile getProfileByUser(User user);

    Profile uploadDocument(User user, MultipartFile file, String type) throws IOException;

    Map<String, String> getMyDocuments(User user);

    String getMyDocumentByType(User user, String type);

    Profile getProfileByUserId(Long userId);

    Profile setVerificationStatus(Long userId, boolean verified);
}