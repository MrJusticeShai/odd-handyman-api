package com.handyman.oddhandyman.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response returned after successful authentication.
 * <p>
 * Contains the JWT access token and its type, which should be used in the
 * Authorization header for subsequent API requests.
 */
@Schema(
        description = "Response returned after successful authentication containing the access token"
)
public class AuthResponse {

    @Schema(
            description = "JWT access token used to authenticate subsequent API requests",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;

    @Schema(
            description = "Type of authentication token",
            example = "Bearer",
            defaultValue = "Bearer"
    )
    private String tokenType = "Bearer";

    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
