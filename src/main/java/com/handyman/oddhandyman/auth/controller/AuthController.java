package com.handyman.oddhandyman.auth.controller;

import com.handyman.oddhandyman.auth.dto.AuthResponse;
import com.handyman.oddhandyman.auth.dto.LoginRequest;
import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.security.JwtUtil;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and authentication context")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, ProfileService profileService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.profileService = profileService;
    }

    @Operation(summary = "Register new user",
            description = "Creates a new user account and initializes an associated user profile"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req);
        profileService.createProfileForUser(u);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    @Operation(summary = "Login user",
            description = "Authenticates a user using email and password and returns a JWT access token"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            String token = jwtUtil.generateToken(req.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @Operation(summary = "Get current user",
            description = "Returns the currently authenticated user's public information",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal UserDetails user
    ) {
        if (user == null) return ResponseEntity.status(401).build();

        String email = user.getUsername();
        User u = userService.findByEmail(email);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }
}
