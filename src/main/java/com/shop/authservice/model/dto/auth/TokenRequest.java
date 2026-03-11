package com.shop.authservice.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {

    @NotBlank(message = "Token must not be blank")
    private String token;

}
