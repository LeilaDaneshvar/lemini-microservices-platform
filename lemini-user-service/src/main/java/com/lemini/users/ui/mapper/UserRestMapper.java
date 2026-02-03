package com.lemini.users.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.lemini.users.shared.dto.UserDto;
import com.lemini.users.ui.model.request.UpdateUserRequestModel;
import com.lemini.users.ui.model.request.UserRequestModel;
import com.lemini.users.ui.model.response.UserRest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRestMapper {

    //Requet to DTO
    UserDto userRequestModelToUserDto(UserRequestModel userRequestModel);

    //DTO to Response
    UserRest userDtoToUserRest(UserDto userDto);

    //Update Request to DTO
    UserDto updateUserRequestModelToUserDto(UpdateUserRequestModel updateUserRequestModel);

    //Update DTO to Response
    UserRest updateUserDtoToUserRest(UserDto userDto);
    
} 