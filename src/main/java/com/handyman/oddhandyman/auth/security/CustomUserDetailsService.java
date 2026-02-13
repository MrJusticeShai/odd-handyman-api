package com.handyman.oddhandyman.auth.security;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of {@link UserDetailsService} for Spring Security.
 * <p>
 * This service loads a user from the database by email and converts it into a
 * Spring Security {@link UserDetails} object that contains the username, password,
 * and granted authorities (roles) for authentication and authorization.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Constructor injection of {@link UserRepository}.
     *
     * @param userRepository repository for retrieving users from the database
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by username (email) and converts it into a Spring Security {@link UserDetails}.
     * <p>
     * Steps performed:
     * 1. Fetch the {@link User} entity from the database by email. <br>
     * 2. Throw {@link UsernameNotFoundException} if the user does not exist. <br>
     * 3. Map the user's role to a {@link GrantedAuthority}. <br>
     * 4. Return a Spring Security {@link org.springframework.security.core.userdetails.User}
     *    containing the email, encoded password, and authorities.
     *</p>
     * @param username the email of the user
     * @return a {@link UserDetails} object for authentication
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        GrantedAuthority ga = new SimpleGrantedAuthority(user.getRole().name());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singleton(ga));
    }
}
