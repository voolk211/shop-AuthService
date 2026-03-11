package com.shop.authservice.model.mappers;

import com.shop.authservice.model.dto.auth.RegistrationRequest;
import com.shop.authservice.model.dto.user.CreateUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    CreateUser toCreateUserRequest(RegistrationRequest registrationRequest);

}
