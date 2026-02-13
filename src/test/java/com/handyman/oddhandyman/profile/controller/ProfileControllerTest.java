package com.handyman.oddhandyman.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.exception.ProfileNotFoundException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private UserService userService;

    private Profile fullMockProfile;
    private User mockUser;
    private final String TEST_EMAIL = "handyman@example.com";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(TEST_EMAIL);
        mockUser.setName("Bob Builder");
        mockUser.setRole(Role.HANDYMAN);

        fullMockProfile = new Profile();
        fullMockProfile.setId(10L);
        fullMockProfile.setUser(mockUser);
        fullMockProfile.setVerified(true);
        fullMockProfile.setAvailable(true);
        fullMockProfile.setPhoneNumber("+27123456789");
        fullMockProfile.setLocation("Cape Town");
        fullMockProfile.setSkills(Arrays.asList("Plumbing", "Electrical"));
        fullMockProfile.setCreatedAt(LocalDateTime.of(2026, 2, 10, 10, 0));

        // Mocking the Map for documents
        Map<String, String> docs = new HashMap<>();
        docs.put("ID_Card", "s3://bucket/id.pdf");
        fullMockProfile.setDocuments(docs);
    }

    @Nested
    @DisplayName("PATCH /api/profile/update")
    class UpdateProfileTests {

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Positive: Should update specific fields via User lookup")
        void updateProfile_Success() throws Exception {
            Profile updateReq = new Profile();
            updateReq.setLocation("Durban");
            updateReq.setAvailable(false);

            when(userService.findByEmail(TEST_EMAIL)).thenReturn(mockUser);
            when(profileService.getProfileByUser(mockUser)).thenReturn(fullMockProfile);
            when(profileService.updateProfile(any(Profile.class))).thenReturn(fullMockProfile);

            mockMvc.perform(patch("/api/profile/update")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.location").value("Durban"))
                    .andExpect(jsonPath("$.available").value(false))
                    .andExpect(jsonPath("$.phoneNumber").value("+27123456789")); // Unchanged
        }
    }

    @Nested
    @DisplayName("GET /api/profile/me")
    class GetProfileTests {

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Positive: Should resolve user then return profile")
        void getProfile_Success() throws Exception {
            when(userService.findByEmail(TEST_EMAIL)).thenReturn(mockUser);
            when(profileService.getProfileByUser(mockUser)).thenReturn(fullMockProfile);

            mockMvc.perform(get("/api/profile/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.id").value(10L));
        }

        @Test
        @WithMockUser(username = "nonexistent@example.com")
        @DisplayName("Negative: Should return 404 when user entity is not found in database")
        void getProfile_UserNotFound_Fails() throws Exception {
            // Mocking the first link in the chain returning null
            when(userService.findByEmail("nonexistent@example.com")).thenThrow(new UserNotFoundException("User Not Found"));

            mockMvc.perform(get("/api/profile/me"))
                    .andExpect(status().isNotFound());

            // Verify we never even tried to call the profile service
            verify(profileService, never()).getProfileByUser(any());
        }

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Negative: Should return 404 when user exists but profile record is missing")
        void getProfile_ProfileNotFound_Fails() throws Exception {
            when(userService.findByEmail(TEST_EMAIL)).thenReturn(mockUser);

            // Mocking the second link in the chain returning null
            when(profileService.getProfileByUser(mockUser)).thenThrow(new ProfileNotFoundException("Profile Not Found"));

            mockMvc.perform(get("/api/profile/me"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Negative: Should return 500 when database connection fails")
        void getProfile_DatabaseError_Fails() throws Exception {
            when(userService.findByEmail(TEST_EMAIL)).thenThrow(new RuntimeException("DB Down"));

            mockMvc.perform(get("/api/profile/me"))
                    .andExpect(status().isInternalServerError());
        }


    }

    @Nested
    @DisplayName("GET /api/profile/search")
    class SearchTests {

        @Test
        @DisplayName("Positive: Should return list based on search params")
        void search_Success() throws Exception {
            when(profileService.searchAvailableHandymen("Plumbing", null, null))
                    .thenReturn(Collections.singletonList(fullMockProfile));

            mockMvc.perform(get("/api/profile/search")
                            .param("skill", "Plumbing"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].skills[0]").value("Plumbing"));
        }
    }

    @Nested
    @DisplayName("Admin Endpoints")
    class AdminTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Positive: Should fetch documents by User ID")
        void getUserDocuments_Success() throws Exception {
            when(profileService.getProfileByUserId(1L)).thenReturn(fullMockProfile);

            mockMvc.perform(get("/api/profile/admin/1/documents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ID_Card").value("s3://bucket/id.pdf"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Positive: Should verify profile status")
        void verifyProfile_Success() throws Exception {
            fullMockProfile.setVerified(true);
            when(profileService.setVerificationStatus(1L, true)).thenReturn(fullMockProfile);

            mockMvc.perform(put("/api/profile/admin/1/verify")
                            .param("verified", "true")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.verified").value(true));
        }
    }
}
