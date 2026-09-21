package com.lemini.users.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.mapper.UserEntityMapper;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.security.CustomUser;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.IdGenerator;
import com.lemini.users.shared.VerificationTokenGenerator;
import com.lemini.users.shared.dto.UserDto;

import java.util.Collections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final int USER_ID_LENGTH = 30;
    public static final int ADDRESS_ID_LENGTH = 30;

    private final UserRepository userRepository;
    private final UserEntityMapper userMapper;
    private final IdGenerator idGenerator;
    private final VerificationTokenGenerator verificationTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    // This method is called by Spring Security for authentication
    @Override
    public UserDetails loadUserByUsername(String email) throws UserServiceException {
        // 1. Find user by email and throw exception if not found
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));

        // 2. Return UserDetails object
        return new CustomUser(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,
                true,
                true,
                Collections.emptyList());
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto user) {

        // Duplicate Email Check
        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw new UserServiceException(UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS);
        }

        // Map DTO -> Entity
        UserEntity userEntity = userMapper.userDtoToUserEntity(user);

        // Business validation - Address Validation
        if (userEntity.getAddresses() == null || userEntity.getAddresses().isEmpty()) {
            throw new UserServiceException(
                    UserServiceException.UserErrorType.VALIDATION_ERROR);
        }

        // Generate User ID and Encrypted Password
        String userId = idGenerator.generateUserId(UserServiceImpl.USER_ID_LENGTH);
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(passwordEncoder.encode(user.password()));
        userEntity.setEmailVerificationToken(verificationTokenGenerator.generateEmailVerificationToken(userId));
        // Email verification is not implemented yet, All new users are marked as verified by default
        userEntity.setEmailVerificationStatus(true);

        // Set Addresses UserEntity Reference
        userEntity.getAddresses().forEach(address -> {
            address.setUserProfile(userEntity);
            address.setAddressId(idGenerator.generateAddressId(UserServiceImpl.ADDRESS_ID_LENGTH));
        });

        // Persist
        UserEntity storedUser = userRepository.save(userEntity);

        return userMapper.userEntityToUserDto(storedUser);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));

        return userMapper.userEntityToUserDto(userEntity);
    }

    @Override
    public UserDto updateUserDto(String userId, UserDto userDto) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));

        userEntity.setFirstName(userDto.firstName());
        userEntity.setLastName(userDto.lastName());

        UserEntity updatedUserEntity = userRepository.save(userEntity);

        return userMapper.userEntityToUserDto(updatedUserEntity);
    }

    @Override
    public void deleteUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));

        userRepository.delete(userEntity);
    }

    @Override
    public Page<UserDto> getUsers(int page, int limit) {

        int pageIndex = page - 1; // API is 1-based; Spring Data is 0-based;

        return userRepository.findAll(PageRequest.of(pageIndex, limit))
                .map(userMapper::userEntityToUserDto);

    }
}