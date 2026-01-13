package com.oddhandyman.auth.controller;

import com.oddhandyman.auth.dto.AuthResponse;
import com.oddhandyman.auth.dto.LoginRequest;
import com.oddhandyman.auth.dto.RegisterRequest;
import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.security.JwtUtil;
import com.oddhandyman.auth.service.UserService;
import com.oddhandyman.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req);
        profileService.createProfileForUser(u);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    @Operation(summary = "Login user")
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

    @Operation(summary = "Get current user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String email = authentication.getName();
        User u = userService.findByEmail(email);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }
}
