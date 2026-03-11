package com.shop.authservice.service.impl;

import feign.FeignException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.shop.authservice.client.UserClient;

import com.shop.authservice.exception.AuthServiceDatabaseException;
import com.shop.authservice.exception.PasswordMismatchException;
import com.shop.authservice.exception.UserServiceException;
import com.shop.authservice.exception.UserServiceUnavailableException;
import com.shop.authservice.exception.ResourceNotFoundException;
import com.shop.authservice.exception.RegistrationFailedException;
import com.shop.authservice.model.dto.auth.AddRoleRequest;
import com.shop.authservice.model.dto.auth.LoginRequest;
import com.shop.authservice.model.dto.auth.RegistrationRequest;
import com.shop.authservice.model.dto.auth.TokenResponse;
import com.shop.authservice.model.dto.user.CreateUser;
import com.shop.authservice.model.entities.RefreshToken;
import com.shop.authservice.model.entities.Role;
import com.shop.authservice.model.entities.User;
import com.shop.authservice.model.mappers.UserMapper;
import com.shop.authservice.repository.RefreshTokenRepository;
import com.shop.authservice.repository.RoleRepository;
import com.shop.authservice.repository.UserRepository;
import com.shop.authservice.service.AuthCredentialService;
import com.shop.authservice.service.AuthService;
import com.shop.authservice.service.jwt.JwtTokenProvider;
import com.shop.authservice.service.jwt.JwtUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Value("${security.jwt.access}")
    private long accessValidityInMs;

    @Value("${security.jwt.refresh}")
    private long refreshValidityInMs;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthCredentialService authCredentialService;

    private final UserClient userClient;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    @Override
    public void register(RegistrationRequest registrationRequest) {
        if (!registrationRequest.getPassword().equals(registrationRequest.getPasswordConfirmation())) {
            throw new PasswordMismatchException("Password and password confirmation do not match.");
        }

        if (userRepository.existsUserByUsername(registrationRequest.getName())) {
            throw new IllegalArgumentException("User already exists.");
        }

        User createdAuthUser;
        try {
            createdAuthUser = authCredentialService.saveCredentials(registrationRequest);
        } catch (DataAccessException ex) {
            throw new AuthServiceDatabaseException("Auth DB error during registration", ex);
        }

        Long authUserId = createdAuthUser.getId();
        try {
            CreateUser userRequest = userMapper.toCreateUserRequest(registrationRequest);
            userRequest.setId(authUserId);
            CreateUser createdUser = userClient.createUser(userRequest);
            if (createdUser == null || createdUser.getId() == null) {
                throw new UserServiceException("User Service returned null id");
            }
        }
        catch (FeignException ex) {
            try {
                authCredentialService.deleteCredentialsCompensate(authUserId);
            } catch (Exception compEx) {
                log.error("Compensation failed for authUserId={}", authUserId, compEx);
            }
            throw new UserServiceUnavailableException("User service unavailable during registration", ex);
        }
        catch (RuntimeException ex) {
            try {
                authCredentialService.deleteCredentialsCompensate(authUserId);
            } catch (Exception compEx) {
                log.error("Compensation failed for authUserId={}", authUserId, compEx);
            }
            throw new RegistrationFailedException("Registration failed", ex);
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
