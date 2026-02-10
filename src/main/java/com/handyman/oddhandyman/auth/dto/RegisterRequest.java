package com.handyman.oddhandyman.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request payload used to register a new user account"
)
public class RegisterRequest {

    @NotBlank
    @Schema(
            description = "Full name of the user",
            example = "John Doe"
    )
    private String name;

    @NotBlank
    @Email
    @Schema(
            description = "Unique email address used for login and communication",
            example = "john.doe@example.com"
    )
    private String email;

    @NotBlank
    @Schema(
            description = "User's plain text password. This value will be securely hashed before storage.",
            example = "P@ssw0rd!"
    )
    private String password;

    @NotNull
    @Schema(
            description = "Role assigned to the user account",
            example = "USER",
            allowableValues = { "USER", "HANDYMAN" }
    )
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
