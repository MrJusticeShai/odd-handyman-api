package com.handyman.oddhandyman.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.auth.dto.LoginRequest;
import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.security.JwtUtil;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.exception.UserConflictException;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    private User fullUser;

    @BeforeEach
    void setUp() {
        fullUser = new User();
        fullUser.setId(1L);
        fullUser.setEmail("test@example.com");
        fullUser.setName("Test User");
        fullUser.setRole(Role.CUSTOMER);
        fullUser.setPassword("should-be-masked");
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Positive: Should register user, create profile, and mask password")
        void register_Success() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setEmail("test@example.com");
            req.setName("Test User");
            req.setPassword("password123");
            req.setRole("CUSTOMER");

            when(userService.register(any(RegisterRequest.class))).thenReturn(fullUser);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"))
                    .andExpect(jsonPath("$.password").isEmpty());

            verify(profileService, times(1)).createProfileForUser(any(User.class));
        }

        @Test
        @DisplayName("Negative: Should return 409 Conflict when user already exists")
        void register_UserAlreadyExists_Fails() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setEmail("existing@example.com");
            req.setName("Test User");
            req.setPassword("password123");
            req.setRole("CUSTOMER");

            when(userService.register(any(RegisterRequest.class)))
                    .thenThrow(new UserConflictException("User Already Exists"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isConflict());

            verify(profileService, never()).createProfileForUser(any());
        }

        @Test
        @DisplayName("Negative: Should return 400 Bad Request when role is invalid")
        void register_InvalidRole_Fails() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setEmail("test@example.com");
            req.setName("Test User");
            req.setPassword("password123");
            req.setRole("INVALID_ROLE");

            // Role.valueOf("INVALID_ROLE") will throw IllegalArgumentException
            when(userService.register(any(RegisterRequest.class)))
                    .thenThrow(new IllegalArgumentException("No enum constant Role.INVALID_ROLE"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative: Should return 400 Bad Request when DTO validation fails (Empty Email)")
        void register_EmptyEmail_Fails() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setEmail(""); // Invalid
            req.setName("Test User");
            req.setPassword("password123");
            req.setRole("CUSTOMER");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).register(any());
        }

    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Positive: Should return JWT token on valid credentials")
        void login_Success() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("test@example.com");
            req.setPassword("password123");

            when(jwtUtil.generateToken("test@example.com")).thenReturn("mock-jwt-token");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("mock-jwt-token"));
        }

        @Test
        @DisplayName("Negative: Should return 401 on bad credentials")
        void login_InvalidCredentials() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("wrong@example.com");
            req.setPassword("wrong");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Invalid credentials"));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class MeTests {
        @Test
        @WithMockUser(username = "test@example.com", roles = "CUSTOMER")
        @DisplayName("Positive: Should return current user using @WithMockUser")
        void me_WithMockUser_Success() throws Exception {
            String email = "test@example.com";

            // Ensure the service returns the object for THIS specific username
            when(userService.findByEmail(email)).thenReturn(fullUser);

            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"))
                    .andExpect(jsonPath("$.password").doesNotExist());

            verify(userService).findByEmail(email);
        }

        @Test
        @DisplayName("Negative: Should return 401 when no principal is present")
        void me_NoUser() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isUnauthorized());
        }
    }

}
