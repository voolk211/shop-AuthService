package org.example.authservice.model.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUser {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;
}
