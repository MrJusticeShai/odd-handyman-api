package com.handyman.oddhandyman.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for creating, parsing, and validating JWT (JSON Web Tokens).
 * <p>
 * Responsibilities: <br>
 * 1. Generate JWT tokens for authenticated users with a configurable expiration. <br>
 * 2. Extract the subject (username/email) from a JWT. <br>
 * 3. Validate JWT tokens to ensure integrity and expiration. <br>
 *
 * Tokens are signed using an HMAC SHA-256 key derived from a secret.
 */
@Component
public class JwtUtil {

    /** Secret key used for signing the JWT */
    private final Key key;

    /** Expiration time in milliseconds for generated tokens */
    private final long expirationMs;

    /**
     * Constructor that initializes the signing key and token expiration from application properties.
     *
     * @param secret the secret string used to generate HMAC key (from ${jwt.secret})
     * @param expirationMs token expiration in milliseconds (from ${jwt.expiration-ms})
     */
    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT token for a given subject (e.g., email).
     *
     * @param subject the subject of the token (typically the user's email)
     * @return a signed JWT as a string
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (email/username) from a JWT token.
     *
     * @param token the JWT token to parse
     * @return the subject contained in the token
     * @throws JwtException if the token is invalid or tampered with
     */
    public String extractSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a JWT token.
     *
     * Checks signature integrity and expiration.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false; // token is expired, invalid, or tampered with
        }
    }
}
