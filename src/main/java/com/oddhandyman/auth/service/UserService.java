package com.oddhandyman.auth.service;

import com.oddhandyman.auth.dto.RegisterRequest;
import com.oddhandyman.auth.entity.User;

public interface UserService {

    User register(RegisterRequest req);

    User findByEmail(String email);
}
