package com.handyman.oddhandyman.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a user in the system.
 * <p>
 * Users can have different roles (CUSTOMER, HANDYMAN, ADMIN), which determine
 * their permissions and available functionality. This entity stores authentication
 * credentials, role, verification status, and creation timestamp.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "User's unique email address used for authentication", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Encrypted password of the user", example = "••••••••")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Role of the user", example = "CUSTOMER", allowableValues = { "CUSTOMER", "HANDYMAN", "ADMIN" })
    private Role role;

    @Column(nullable = false)
    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Indicates if the user's email has been verified", example = "false")
    private boolean verified = false;

    @Schema(description = "Timestamp when the user was created", example = "2026-02-10T14:30:00Z")
    private Instant createdAt = Instant.now();

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
