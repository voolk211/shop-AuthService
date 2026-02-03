package org.example.authservice.service;

import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.TokenResponse;
import org.example.authservice.model.entities.User;

public interface AuthService {

    User register(User user);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshTokens(String refreshToken);

    void validateToken(String accessToken);
}
