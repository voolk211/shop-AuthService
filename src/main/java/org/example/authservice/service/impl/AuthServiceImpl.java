package org.example.authservice.service.impl;

import feign.FeignException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.authservice.client.UserClient;
import org.example.authservice.exception.PasswordMismatchException;
import org.example.authservice.exception.ResourceNotFoundException;
import org.example.authservice.exception.UserServiceUnavailableException;
import org.example.authservice.model.dto.auth.AddRoleRequest;
import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.RegistrationRequest;
import org.example.authservice.model.dto.auth.TokenResponse;
import org.example.authservice.model.dto.user.CreateUser;
import org.example.authservice.model.entities.RefreshToken;
import org.example.authservice.model.entities.Role;
import org.example.authservice.model.entities.RoleName;
import org.example.authservice.model.entities.User;
import org.example.authservice.model.mappers.UserMapper;
import org.example.authservice.repository.RefreshTokenRepository;
import org.example.authservice.repository.RoleRepository;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.service.AuthService;
import org.example.authservice.service.jwt.JwtTokenProvider;
import org.example.authservice.service.jwt.JwtUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
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
@Log4j2
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

    private final UserClient userClient;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        if (!registrationRequest.getPassword().equals(registrationRequest.getPasswordConfirmation())) {
            throw new PasswordMismatchException("Password and password confirmation do not match.");
        }

        if (userRepository.existsUserByUsername(registrationRequest.getName())) {
            throw new IllegalArgumentException("User already exists.");
        }

        Long userId = null;
        try {
            CreateUser userRequest = userMapper.toCreateUserRequest(registrationRequest);
            CreateUser createdUser = userClient.createUser(userRequest);

            User user = new User();
            Role role = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));

            user.setRoles(Set.of(role));
            userId = createdUser.getId();
            user.setId(createdUser.getId());
            user.setUsername(createdUser.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            userRepository.save(user);

        }
        catch (FeignException | DataAccessException e) {
            if (userId != null) {
                try {
                    userClient.deleteUser(userId);
                } catch (Exception ex) {
                    log.error("Compensation failed for userId {}", userId, ex);
                }
            }
            throw new UserServiceUnavailableException("Registration failed", e);
        }
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        User user = userRepository.getReferenceById(userDetails.getId());

        RefreshToken entityRefreshToken = createRefreshToken(user, userDetails);
        String accessToken = createAccessToken(userDetails);
        String refreshToken = entityRefreshToken.getToken();

        refreshTokenRepository.save(entityRefreshToken);

        return new TokenResponse(user.getUsername(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refreshTokens(String token) {
        jwtTokenProvider.validateRefreshToken(token);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new JwtException("Refresh token not found"));

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
    public void validateToken(String token) {
        jwtTokenProvider.validateAccessToken(token);
    }

    @Override
    @Transactional
    public void addRoleToUser(Long userId, AddRoleRequest addRoleRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findByName(addRoleRequest.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
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


}
