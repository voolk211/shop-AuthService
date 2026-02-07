package org.example.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.model.dto.auth.AddRoleRequest;
import org.example.authservice.model.dto.auth.LoginRequest;
import org.example.authservice.model.dto.auth.TokenRequest;
import org.example.authservice.model.dto.auth.TokenResponse;
import org.example.authservice.model.dto.user.UserDto;
import org.example.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserDto userDto){
        authService.register(userDto);
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> addRole(@PathVariable Long id, @RequestBody AddRoleRequest addRoleRequest){
        authService.addRoleToUser(id, addRoleRequest);
        return ResponseEntity.ok().build();
    }

}