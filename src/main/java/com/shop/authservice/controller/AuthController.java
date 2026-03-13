package com.shop.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.shop.authservice.model.dto.auth.RegistrationRequest;
import com.shop.authservice.model.dto.auth.TokenResponse;
import com.shop.authservice.model.dto.auth.TokenRequest;
import com.shop.authservice.model.dto.auth.LoginRequest;
import com.shop.authservice.model.dto.auth.AddRoleRequest;
import com.shop.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest registrationRequest){
        authService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRequest tokenRequest){
        TokenResponse tokenResponse = authService.refreshTokens(tokenRequest.getToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(@Valid @RequestBody TokenRequest tokenRequest){
        authService.validateToken(tokenRequest.getToken());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> addRole(@PathVariable Long id, @Valid @RequestBody AddRoleRequest addRoleRequest){
        authService.addRoleToUser(id, addRoleRequest);
        return ResponseEntity.ok().build();
    }
}