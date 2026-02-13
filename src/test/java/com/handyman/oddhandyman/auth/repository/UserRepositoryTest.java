package com.handyman.oddhandyman.auth.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager; // Used to setup data without using the repository itself

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_ReturnsTrue() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("John Doe");
        user.setPassword("secret");
        user.setRole(Role.CUSTOMER);

        entityManager.persist(user); // Save directly to H2
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return empty Optional when email does not exist")
    void findByEmail_ReturnsEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find user by email correctly")
    void findByEmail_ReturnsUser() {
        // Given
        User user = new User();
        user.setEmail("findme@example.com");
        user.setName("Target User");
        user.setPassword("password");
        user.setRole(Role.HANDYMAN);
        entityManager.persist(user);

        // When
        Optional<User> found = userRepository.findByEmail("findme@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Target User", found.get().getName());
    }
}
