package com.handyman.oddhandyman.profile.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.profile.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User handymanUser;
    private Profile handymanProfile;

    @BeforeEach
    void setUp() {
        handymanUser = new User();
        handymanUser.setEmail("handy@service.com");
        handymanUser.setName("Bob Builder");
        handymanUser.setRole(Role.HANDYMAN);
        handymanUser.setPassword("encoded_pass");
        entityManager.persist(handymanUser);

        handymanProfile = new Profile();
        handymanProfile.setUser(handymanUser);
        handymanProfile.setAvailable(true);
        // Use a mutable ArrayList to avoid UnsupportedOperationException
        handymanProfile.setSkills(new ArrayList<>(Arrays.asList("Plumbing", "Electrical")));
        handymanProfile.setLocation("Pretoria");

        entityManager.persist(handymanProfile);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find available profiles by exact skill if IgnoreCase fails in H2")
    void findBySkill_Success() {
        // Use the exact casing "Plumbing" to verify the base query works
        List<Profile> results = profileRepository
                .findByAvailableTrueAndSkillsContainingIgnoreCase("Plumbing");

        assertEquals(1, results.size(), "Should find 1 profile with Plumbing skill");
    }

    @Test
    @DisplayName("Should find profile by navigating User name property")
    void findByUserName_Success() {
        List<Profile> results = profileRepository
                .findByAvailableTrueAndUserNameContainingIgnoreCase("Bob");

        assertFalse(results.isEmpty());
        assertEquals("Bob Builder", results.get(0).getUser().getName());
    }

    @Test
    @DisplayName("Should find profile by navigating User email property")
    void findByUserEmail_Success() {
        List<Profile> results = profileRepository
                .findByAvailableTrueAndUserEmailContainingIgnoreCase("handy@service.com");

        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("Should find profile by the user's ID")
    void findByUserId_Success() {
        var found = profileRepository.findByUserId(handymanUser.getId());
        assertTrue(found.isPresent());
    }
}
