package org.example.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.exception.PasswordMismatchException;
import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.TokenRequest;
import org.example.authservice.model.dto.auth.TokenResponse;
import org.example.authservice.model.dto.user.UserDto;
import org.example.authservice.model.entities.User;
import org.example.authservice.model.mappers.UserMapper;
import org.example.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserDto userDto){
        if (!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
            throw new PasswordMismatchException("Password and password confirmation do not match.");
        }
        User user = userMapper.toEntity(userDto);
        authService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRequest tokenRequest){
        TokenResponse tokenResponse = authService.refreshTokens(tokenRequest.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(@Valid @RequestBody TokenRequest tokenRequest){
        authService.validateToken(tokenRequest.getToken());
        return ResponseEntity.ok().build();
    }

}