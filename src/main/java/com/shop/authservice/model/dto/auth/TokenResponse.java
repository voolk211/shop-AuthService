package com.shop.authservice.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String username;
    private String accessToken;
    private String refreshToken;

}
