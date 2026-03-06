package org.example.authservice.service;

import org.example.authservice.model.dto.auth.AddRoleRequest;
import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.RegistrationRequest;
import org.example.authservice.model.dto.auth.TokenResponse;

public interface AuthService {

    void register(RegistrationRequest registrationRequest);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshTokens(String refreshToken);

    void validateToken(String accessToken);

    void addRoleToUser(Long userId, AddRoleRequest addRoleRequest);
}
