package com.handyman.oddhandyman.auth.dto;

import com.handyman.oddhandyman.auth.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Public user representation returned in API responses. Sensitive fields are intentionally excluded."
)
public class UserResponse {

    @Schema(
            description = "Unique identifier of the user",
            example = "42"
    )
    private Long id;

    @Schema(
            description = "Full name of the user",
            example = "John Doe"
    )
    private String name;

    @Schema(
            description = "Email address of the user",
            example = "john.doe@example.com"
    )
    private String email;

    @Schema(
            description = "Role assigned to the user account",
            example = "HANDYMAN",
            allowableValues = { "CUSTOMER", "HANDYMAN", "ADMIN" }
    )
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
