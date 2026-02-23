package org.example.authservice.model.mappers;

import org.example.authservice.model.dto.user.UserDto;
import org.example.authservice.model.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto userDto);

}
