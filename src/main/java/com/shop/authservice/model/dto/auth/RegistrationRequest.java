package com.shop.authservice.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Name must not be blank")
    @Length(max = 255, message = "Name length must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Password must not be blank")
    @Length(min = 3, max = 255, message = "Password length must be between 3 and 255 characters")
    private String password;

    @NotBlank(message = "Password confirmation must not be blank")
    @Length(min = 3, max = 255, message = "Password confirmation length must be between 3 and 255 characters")
    private String passwordConfirmation;

    @NotBlank(message = "Surname must not be blank")
    @Length(max = 255, message = "Surname length must not exceed 255 characters")
    private String surname;

    @NotNull(message = "BirthDate must not be null")
    private LocalDate birthDate;

    @NotBlank(message = "Email must not be blank")
    @Length(max = 255, message = "Email length must not exceed 255 characters")
    @Email
    private String email;

    @NotNull(message = "Active must not be null")
    private Boolean active;

}
