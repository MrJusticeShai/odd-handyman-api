package com.oddhandyman.auth.service.impl;

import com.oddhandyman.auth.dto.RegisterRequest;
import com.oddhandyman.auth.entity.Role;
import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.repository.UserRepository;
import com.oddhandyman.auth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.valueOf(req.getRole()));

        return userRepository.save(u);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
