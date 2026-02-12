package com.lemini.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.io.entity.AddressEntity;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.mapper.UserEntityMapper;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.shared.dto.AddressDto;
import com.lemini.users.shared.dto.UserDto;
import com.lemini.users.shared.Utils;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserEntityMapper userMapper;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    UserDto userDto;
    UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        // Prepare test data

        // UserDto with one address
        AddressDto addressDto = new AddressDto(1L, "addr123", "123 Street", "CityX", "CountryY", "12345", "shipping");

        userDto = new UserDto(
                1L, "user123", "user1", "family1", "test@test.com",
                "password123", "encPass", "token123", false, List.of(addressDto));

        // UserEntity with one address
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);
        addressEntity.setAddressId("addr123");
        addressEntity.setStreetName("123 Street");
        addressEntity.setCity("CityX");
        addressEntity.setCountry("CountryY");
        addressEntity.setPostalCode("12345");
        addressEntity.setType("shipping");
        addressEntity.setUserProfile(userEntity);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("user1");
        userEntity.setEmail("test@test.com");
        userEntity.setEncryptedPassword("encPass");
        userEntity.setEmailVerificationToken("token123");
        userEntity.setAddresses(List.of(addressEntity));
    }

    @Test
    void testCreateUser_HappyPath() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // No duplicate
        when(userMapper.userDtoToUserEntity(any(UserDto.class))).thenReturn(userEntity);
        when(utils.generateUserId(anyInt())).thenReturn("generatedUserId");
        when(utils.generateEmailVerificationToken(anyString())).thenReturn("genToken");
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.userEntityToUserDto(any(UserEntity.class))).thenReturn(userDto);
        when(utils.generateAddressId(anyInt())).thenReturn("addrId123");

        // When
        UserDto createdUser = userService.createUser(userDto);

        // Then
        assertNotNull(createdUser);
        assertEquals("user1", createdUser.firstName());

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(utils, times(1)).generateUserId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("password123");

    }

    @Test
    void testCreateUser_DuplicateEmail() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity)); // Duplicate

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.createUser(userDto);
        });
        ;

        assertEquals(UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS, exception.getErrorType());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testLoadUserByUsername_HappyPath() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        // When
        var userDetails = userService.loadUserByUsername("test@test.com");

        // Then
        assertNotNull(userDetails);
        assertEquals(userEntity.getEmail(), userDetails.getUsername());
        assertEquals(userEntity.getEncryptedPassword(), userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void testGetUserByUserId_HappyPath() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(userEntity));
        when(userMapper.userEntityToUserDto(any(UserEntity.class))).thenReturn(userDto);
        // When
        var user = userService.getUserByUserId("user123");
        // Then
        assertNotNull(user);
        assertEquals("user1", user.firstName());
    }

    @Test
    void testGetUserByUserId_UserNotFound() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.getUserByUserId("nonexistentUserId");
        });
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void testupdateUserDto_HappyPath() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.userEntityToUserDto(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity entity = invocation.getArgument(0);
                    return new UserDto(
                            entity.getId(),
                            entity.getUserId(),
                            entity.getFirstName(),
                            entity.getLastName(),
                            entity.getEmail(),
                            "",
                            entity.getEncryptedPassword(),
                            entity.getEmailVerificationToken(),
                            entity.getEmailVerificationStatus(),
                            null // or map addresses if needed
                    );
                });
        UserDto updatedInfo = new UserDto(
                1L, "user123", "newFirstName", "newFamilyName", "", "", "", "", false, null);
        // When
        var updatedUser = userService.updateUserDto("user123", updatedInfo);
        // Then
        assertNotNull(updatedUser);
        assertEquals("newFirstName", updatedUser.firstName());
        assertEquals("newFamilyName", updatedUser.lastName());
    }

    @Test
    void testupdateUserDto_UserNotFound() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        UserDto updatedInfo = new UserDto(
                1L, "user123", "newFirstName", "newFamilyName", "", "", "", "", false, null);
        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.updateUserDto("nonexistentUserId", updatedInfo);
        });
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void testDeleteUser_HappyPath() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(userEntity));
        // When
        userService.deleteUserByUserId("user123");
        // Then
        verify(userRepository, times(1)).delete(any(UserEntity.class));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.deleteUserByUserId("nonexistentUserId");
        });
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void testGetUsers_HappyPath() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.userEntityToUserDto(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity entity = invocation.getArgument(0);
                    return new UserDto(
                            entity.getId(),
                            entity.getUserId(),
                            entity.getFirstName(),
                            entity.getLastName(),
                            entity.getEmail(),
                            "",
                            entity.getEncryptedPassword(),
                            entity.getEmailVerificationToken(),
                            entity.getEmailVerificationStatus(),
                            null);
                });
        // When
        var users = userService.getUsers(0, 2);
        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("user1", users.get(0).firstName());
    }

    @Test
    void testGetUsers_EmptyList() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> page = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        var users = userService.getUsers(0, 2);
        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

}