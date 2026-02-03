package org.example.authservice.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.TokenResponse;
import org.example.authservice.model.entities.RefreshToken;
import org.example.authservice.model.entities.Role;
import org.example.authservice.model.entities.User;
import org.example.authservice.repository.RefreshTokenRepository;
import org.example.authservice.repository.RoleRepository;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.service.AuthService;
import org.example.authservice.service.jwt.JwtTokenProvider;
import org.example.authservice.service.jwt.JwtUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${security.jwt.access}")
    private long accessValidityInMs;

    @Value("${security.jwt.refresh}")
    private long refreshValidityInMs;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User register(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User already exists.");
        }
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));

        user.setRoles(Set.of(role));
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        return userRepository.save(user);
    }

    private RefreshToken createRefreshToken(User user, JwtUserDetails userDetails){
        Instant now = Instant.now();
        Instant refreshExpiresAt = now.plusMillis(refreshValidityInMs);

        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails, now, refreshExpiresAt);

        RefreshToken entityRefreshToken = new RefreshToken();
        entityRefreshToken.setToken(refreshToken);
        entityRefreshToken.setUser(user);
        entityRefreshToken.setCreatedAt(now);
        entityRefreshToken.setExpiresAt(refreshExpiresAt);

        return entityRefreshToken;
    }

    private String createAccessToken(JwtUserDetails userDetails){
        Instant now = Instant.now();
        Instant accessExpiresAt = now.plusMillis(accessValidityInMs);

        return jwtTokenProvider.generateAccessToken(userDetails, now, accessExpiresAt);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        User user = userRepository.getReferenceById(userDetails.getId());

        Instant now = Instant.now();
        Instant accessExpiresAt = now.plusMillis(accessValidityInMs);
        Instant refreshExpiresAt = now.plusMillis(refreshValidityInMs);

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails, now, accessExpiresAt);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails, now, refreshExpiresAt);

        RefreshToken entityRefreshToken = new RefreshToken();
        entityRefreshToken.setToken(refreshToken);
        entityRefreshToken.setUser(user);
        entityRefreshToken.setCreatedAt(now);
        entityRefreshToken.setExpiresAt(refreshExpiresAt);

        refreshTokenRepository.save(entityRefreshToken);

        return new TokenResponse(user.getUsername(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refreshTokens(String token) {
        jwtTokenProvider.validateRefreshToken(token);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new JwtException("Refresh token not found"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new JwtException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        JwtUserDetails userDetails = new JwtUserDetails(user);

        RefreshToken entityRefreshToken = createRefreshToken(user, userDetails);
        String newAccessToken = createAccessToken(userDetails);
        String newRefreshToken = entityRefreshToken.getToken();

        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.save(entityRefreshToken);

        return new TokenResponse(user.getUsername(), newAccessToken, newRefreshToken);
    }


    @Override
    @Transactional
    public void validateToken(String token) {
        jwtTokenProvider.validateAccessToken(token);
    }


}
