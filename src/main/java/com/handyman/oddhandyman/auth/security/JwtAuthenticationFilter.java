package com.handyman.oddhandyman.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that authenticates incoming HTTP requests using JWT tokens.
 * <p>
 * This filter executes once per request and performs the following steps: <br>
 * 1. Extracts the JWT token from the 'Authorization' header (Bearer scheme). <br>
 * 2. Validates the token using {@link JwtUtil}. <br>
 * 3. Extracts the username (subject) from the token. <br>
 * 4. Loads the corresponding {@link UserDetails} using {@link UserDetailsService}. <br>
 * 5. Sets the authentication in the {@link SecurityContextHolder} so that
 *    downstream Spring Security filters recognize the user as authenticated. <br>
 *
 * This allows endpoints secured with Spring Security annotations (e.g., @PreAuthorize)
 * to access the authenticated user's details.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtUtil utility class for JWT operations (validate, extract subject, etc.)
     * @param userDetailsService service to load user details by username
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests to authenticate the user via JWT.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain to continue processing
     * @throws ServletException if filter processing fails
     * @throws IOException if an I/O error occurs during processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Extract JWT token from Authorization header
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            if (jwtUtil.validateToken(token)) { username = jwtUtil.extractSubject(token); }
        }
        // Authenticate user if not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
