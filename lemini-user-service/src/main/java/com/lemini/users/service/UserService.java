package com.lemini.users.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.lemini.users.shared.dto.UserDto;


public interface UserService extends UserDetailsService {

    //Called by Spring Security
    @Override
    UserDetails loadUserByUsername(String email);

    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    UserDto updateUserDto(String userId, UserDto userDto);
}
