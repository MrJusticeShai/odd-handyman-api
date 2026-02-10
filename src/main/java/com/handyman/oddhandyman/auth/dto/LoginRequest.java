package com.handyman.oddhandyman.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        description = "Request payload used to authenticate a user using email and password"
)
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(
            description = "Registered email address of the user",
            example = "user@example.com"
    )
    private String email;

    @NotBlank
    @Schema(
            description = "User's plain text password. This value is never stored or returned.",
            example = "P@ssw0rd!"
    )
    private String password;

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
}
