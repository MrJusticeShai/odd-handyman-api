package com.handyman.oddhandyman.auth.service.impl;

import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.exception.UserConflictException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("Test User");
        registerRequest.setPassword("rawPassword");
        registerRequest.setRole("CUSTOMER");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setRole(Role.CUSTOMER);
        mockUser.setPassword("encodedPassword");
    }

    @Nested
    @DisplayName("register() Tests")
    class RegisterTests {

        @Test
        @DisplayName("Positive: Should successfully register a new user")
        void register_Success() {
            // Given
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // When
            User result = userService.register(registerRequest);

            // Then
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            assertEquals(Role.CUSTOMER, result.getRole());

            // Verify internal interactions
            verify(passwordEncoder).encode("rawPassword");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Negative: Should throw UserConflictException when email exists")
        void register_EmailExists_ThrowsConflict() {
            // Given
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            // When & Then
            UserConflictException exception = assertThrows(UserConflictException.class, () -> {
                userService.register(registerRequest);
            });

            assertEquals("User Already Exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Negative: Should throw IllegalArgumentException for invalid role")
        void register_InvalidRole_ThrowsException() {
            // Given
            registerRequest.setRole("GOD_MODE"); // Invalid role
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                userService.register(registerRequest);
            });
        }
    }

    @Nested
    @DisplayName("findByEmail() Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Positive: Should return user when email is found")
        void findByEmail_Found() {
            // Given
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

            // When
            User result = userService.findByEmail("test@example.com");

            // Then
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
        }

        @Test
        @DisplayName("Negative: Should throw UserNotFoundException when email not found")
        void findByEmail_NotFound_ThrowsException() {
            // Given
            when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

            // When & Then
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                userService.findByEmail("missing@example.com");
            });

            assertEquals("User Not Found", exception.getMessage());
        }
    }
}
