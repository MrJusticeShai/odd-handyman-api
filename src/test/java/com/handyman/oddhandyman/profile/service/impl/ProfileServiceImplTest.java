package com.handyman.oddhandyman.profile.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.exception.ProfileNotFoundException;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User mockUser;
    private Profile mockProfile;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.HANDYMAN);

        mockProfile = new Profile();
        mockProfile.setId(10L);
        mockProfile.setUser(mockUser);
    }

    @Nested
    @DisplayName("createProfileForUser() Logic")
    class CreateProfileTests {

        @Test
        @DisplayName("Positive: Should create AVAILABLE profile for HANDYMAN")
        void createProfile_Handyman_Available() {
            when(profileRepository.findByUser(mockUser)).thenReturn(Optional.empty());
            when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));

            Profile result = profileService.createProfileForUser(mockUser);

            assertTrue(result.isAvailable(), "Handyman should be available by default");
            assertEquals(mockUser, result.getUser());
            verify(profileRepository).save(any(Profile.class));
        }

        @Test
        @DisplayName("Positive: Should create UNAVAILABLE profile for CUSTOMER")
        void createProfile_Customer_Unavailable() {
            mockUser.setRole(Role.CUSTOMER);
            when(profileRepository.findByUser(mockUser)).thenReturn(Optional.empty());
            when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));

            Profile result = profileService.createProfileForUser(mockUser);

            assertFalse(result.isAvailable(), "Customer should not be available by default");
        }

        @Test
        @DisplayName("Positive: Should return existing profile if already present")
        void createProfile_Exists_ReturnExisting() {
            when(profileRepository.findByUser(mockUser)).thenReturn(Optional.of(mockProfile));

            Profile result = profileService.createProfileForUser(mockUser);

            assertEquals(10L, result.getId());
            verify(profileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("searchAvailableHandymen() Logic")
    class SearchTests {

        @Test
        @DisplayName("Positive: Should filter by skill if provided")
        void search_BySkill() {
            profileService.searchAvailableHandymen("Plumbing", null, null);
            verify(profileRepository).findByAvailableTrueAndSkillsContainingIgnoreCase("Plumbing");
        }

        @Test
        @DisplayName("Positive: Should filter by name if skill is null")
        void search_ByName() {
            profileService.searchAvailableHandymen(null, "John", null);
            verify(profileRepository).findByAvailableTrueAndUserNameContainingIgnoreCase("John");
        }

        @Test
        @DisplayName("Positive: Should return all available if no params provided")
        void search_AllAvailable() {
            profileService.searchAvailableHandymen(null, "", null);
            verify(profileRepository).findByAvailableTrue();
        }
    }

    @Nested
    @DisplayName("Retrieval and Verification Logic")
    class RetrievalTests {

        @Test
        @DisplayName("Positive: Should update verification status")
        void setVerification_Success() {
            when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(mockProfile));
            when(profileRepository.save(any(Profile.class))).thenReturn(mockProfile);

            Profile result = profileService.setVerificationStatus(1L, true);

            assertTrue(result.isVerified());
            verify(profileRepository).save(mockProfile);
        }

        @Test
        @DisplayName("Negative: Should throw ProfileNotFoundException when user profile missing")
        void getProfile_NotFound_ThrowsException() {
            when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

            assertThrows(ProfileNotFoundException.class, () -> profileService.getProfileByUserId(1L));
        }
    }
}
