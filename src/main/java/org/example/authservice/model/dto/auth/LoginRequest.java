package org.example.authservice.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    @Length(min=3, max = 255, message = "Username length must be between 3 and 255 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Length(min = 3, max = 255, message = "Password length must be between 3 and 255 characters")
    private String password;

}
