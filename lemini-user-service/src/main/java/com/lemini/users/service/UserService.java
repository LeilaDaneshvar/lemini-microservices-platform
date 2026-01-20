package com.lemini.users.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.lemini.users.shared.dto.UserDto;


public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);
    //Calles by Spring Security
    @Override
    UserDetails loadUserByUsername(String email);

}
