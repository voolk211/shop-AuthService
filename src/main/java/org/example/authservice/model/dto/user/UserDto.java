package org.example.authservice.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {

    @NotBlank(message = "Username must not be blank")
    @Length(min=3, max = 255, message = "Username length must not exceed 255 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Length(min = 3, max = 255, message = "Password length must be between 3 and 255 characters")
    private String password;

    @NotBlank(message = "Password confirmation must not be blank")
    @Length(min = 3, max = 255, message = "Password confirmation length must be between 3 and 255 characters")
    private String passwordConfirmation;

}
