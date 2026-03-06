package org.example.authservice.model.mappers;

import org.example.authservice.model.dto.auth.RegistrationRequest;
import org.example.authservice.model.dto.user.CreateUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    CreateUser toCreateUserRequest(RegistrationRequest registrationRequest);

}
