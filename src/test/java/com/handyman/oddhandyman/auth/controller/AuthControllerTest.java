package com.handyman.oddhandyman.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.auth.dto.LoginRequest;
import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.security.JwtUtil;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.profile.entity.Profile;
import com.handyman.oddhandyman.profile.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    // ---------------- Register Tests ----------------

    @Test
    void register_ShouldReturnUser_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setRole("CUSTOMER");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(request.getEmail());
        mockUser.setName(request.getName());
        mockUser.setRole(Role.CUSTOMER);

        Profile mockProfile = new Profile();
        mockProfile.setUser(mockUser);
        mockProfile.setVerified(false);
        mockProfile.setAvailable(false);
        mockProfile.setCreatedAt(LocalDateTime.now());
        mockProfile.setSkills(new ArrayList<>());
        mockProfile.setRating(0.0);

        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);
        when(profileService.createProfileForUser(any(User.class))).thenReturn(mockProfile);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()))
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()))
                .andExpect(jsonPath("$.name").value(mockUser.getName()))
                .andExpect(jsonPath("$.role").value(mockUser.getRole().name()))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).register(any(RegisterRequest.class));
        verify(profileService).createProfileForUser(any(User.class));
    }

    @Test
    void register_ShouldReturnUser_WhenHandymanRole() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("handyman@example.com");
        request.setPassword("password123");
        request.setName("Bob Builder");
        request.setRole("HANDYMAN");

        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setEmail(request.getEmail());
        mockUser.setName(request.getName());
        mockUser.setRole(Role.HANDYMAN);

        Profile mockProfile = new Profile();
        mockProfile.setUser(mockUser);
        mockProfile.setVerified(false);
        mockProfile.setAvailable(true); // Handyman is available by default
        mockProfile.setCreatedAt(LocalDateTime.now());
        mockProfile.setSkills(new ArrayList<>());
        mockProfile.setRating(0.0);

        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);
        when(profileService.createProfileForUser(any(User.class))).thenReturn(mockProfile);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()))
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()))
                .andExpect(jsonPath("$.name").value(mockUser.getName()))
                .andExpect(jsonPath("$.role").value(mockUser.getRole().name()))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).register(any(RegisterRequest.class));
        verify(profileService).createProfileForUser(any(User.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email"); // Invalid email

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
        verify(profileService, never()).createProfileForUser(any());
    }

    // ---------------- Login Tests ----------------

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Authentication mockAuth = mock(Authentication.class);
        String expectedToken = "jwt-token-12345";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtUtil.generateToken(anyString())).thenReturn(expectedToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("test@example.com");
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com"); // missing password

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    // ---------------- Me Tests ----------------

    // TODO: Implement the getMe endpoint test
    @Test
    void me_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).findByEmail(anyString());
    }
}
