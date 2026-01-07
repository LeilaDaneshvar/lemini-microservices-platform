package com.lemini.users.io.mapper;

import org.mapstruct.Mapper;

import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.shared.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    //Entity to DTO
    UserDto userEntityToUserDto(UserEntity userEntity);

    //DTO to Entity
    UserEntity userDtoToUserEntity(UserDto userDto);
}
