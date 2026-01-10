package com.lemini.users.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.mapper.UserEntityMapper;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.Utils;
import com.lemini.users.shared.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityMapper userMapper;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Transactional
    @Override
    public UserDto createUser(UserDto user) {

        // 1. Duplicate Check
        if(userRepository.findByEmail(user.email()).isPresent()) {
            throw new UserServiceException(UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS);
        }
        
        // 2. Map Record -> Entity
        UserEntity userEntity = userMapper.userDtoToUserEntity(user);

        // 3. Generate User ID and Encrypted Password
        userEntity.setUserId(utils.generateUserId(30));
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.password()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userEntity.getUserId()));
        userEntity.setEmailVerificationStatus(false);

        // 4. Set Addresses UserEntity Reference
        if(userEntity.getAddresses() != null) {
            userEntity.getAddresses().forEach(address -> {
                address.setUserProfile(userEntity);
                address.setAddressId(utils.generateAddressId(30));
            });
        }

        // 5. Save User
        UserEntity storedUser = userRepository.save(userEntity);
        
        return userMapper.userEntityToUserDto(storedUser);
    }

}
