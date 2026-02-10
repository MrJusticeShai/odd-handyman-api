package com.handyman.oddhandyman.auth.service;

import com.handyman.oddhandyman.auth.dto.RegisterRequest;
import com.handyman.oddhandyman.auth.entity.User;

public interface UserService {

    User register(RegisterRequest req);

    User findByEmail(String email);
}
