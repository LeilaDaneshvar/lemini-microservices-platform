package com.lemini.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.io.entity.AddressEntity;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.mapper.UserEntityMapper;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.shared.dto.AddressDto;
import com.lemini.users.shared.dto.UserDto;

import com.lemini.users.shared.IdGenerator;
import com.lemini.users.shared.VerificationTokenGenerator;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserEntityMapper userMapper;

    @Mock
    IdGenerator idGenerator;

    @Mock
    VerificationTokenGenerator verificationTokenGenerator;

    @Mock
    PasswordEncoder passwordEncoder;

    UserDto userDto;
    UserEntity userEntity;

    private static final String TEST_USER_ID = "user123";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String GENERATED_USER_ID = "generatedUserId";
    private static final String TEST_FIRST_NAME = "user1";
    private static final String NONEXISTENT_USER_ID = "nonexistentUserId";

    @BeforeEach
    void setUp() {

        AddressDto addressDto = new AddressDto(
                null,
                null,
                "123 Street",
                "CityX",
                "CountryY",
                "12345",
                "shipping");

        userDto = new UserDto(
                null,
                null,
                TEST_FIRST_NAME,
                "family1",
                TEST_EMAIL,
                RAW_PASSWORD,
                null,
                null,
                false,
                List.of(addressDto));

        userEntity = new UserEntity();
        userEntity.setFirstName(TEST_FIRST_NAME);
        userEntity.setLastName("family1");
        userEntity.setEmail(TEST_EMAIL);

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setStreetName("123 Street");
        addressEntity.setCity("CityX");
        addressEntity.setCountry("CountryY");
        addressEntity.setPostalCode("12345");
        addressEntity.setType("shipping");

        userEntity.setAddresses(List.of(addressEntity));
    }

    @Test
    void createUser_whenValidUser_shouldPrepareAndSaveUser() {

        // ** Given

        UserDto storedUserDto = new UserDto(
                1L,
                GENERATED_USER_ID,
                TEST_FIRST_NAME,
                "family1",
                TEST_EMAIL,
                null,
                null,
                null,
                true,
                userDto.addresses());

        // Duplicate Email Check
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Map DTO to Entity
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(userEntity);

        // Generated Values
        when(idGenerator.generateUserId(UserServiceImpl.USER_ID_LENGTH)).thenReturn(GENERATED_USER_ID);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(verificationTokenGenerator.generateEmailVerificationToken(GENERATED_USER_ID)).thenReturn("genToken");
        when(idGenerator.generateAddressId(UserServiceImpl.ADDRESS_ID_LENGTH)).thenReturn("addrId123");

        // Mock persistence
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Map Entity to DTO
        when(userMapper.userEntityToUserDto(any(UserEntity.class))).thenReturn(storedUserDto);

        // ** When
        UserDto result = userService.createUser(userDto);

        // ** Then
        assertNotNull(result);
        assertEquals(GENERATED_USER_ID, result.userId());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();

        // Field Assertions
        assertEquals(TEST_FIRST_NAME, savedUser.getFirstName());
        assertEquals(GENERATED_USER_ID, savedUser.getUserId());
        assertEquals(ENCODED_PASSWORD, savedUser.getEncryptedPassword());
        assertEquals("genToken", savedUser.getEmailVerificationToken());
        assertTrue(savedUser.getEmailVerificationStatus());

        // Nested Collection Assertions
        assertNotNull(savedUser.getAddresses());
        assertEquals(1, savedUser.getAddresses().size());

        AddressEntity savedAddress = savedUser.getAddresses().get(0);
        assertEquals("addrId123", savedAddress.getAddressId());
        assertSame(savedUser, savedAddress.getUserProfile());

        // Verify Method Invocations
        verify(idGenerator).generateUserId(UserServiceImpl.USER_ID_LENGTH);
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(verificationTokenGenerator).generateEmailVerificationToken(GENERATED_USER_ID);
        verify(idGenerator).generateAddressId(UserServiceImpl.ADDRESS_ID_LENGTH);
    }

    @Test
    void createUser_whenEmailAlreadyExists_shouldThrowException() {
        // Given
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(userEntity)); // Duplicate

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.createUser(userDto));

        // Then
        assertEquals(UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS, exception.getErrorType());

        verify(userRepository).findByEmail(userDto.email());

        verify(userMapper, never()).userDtoToUserEntity(any());
        verify(idGenerator, never()).generateUserId(anyInt());
        verify(passwordEncoder, never()).encode(anyString());
        verify(verificationTokenGenerator, never()).generateEmailVerificationToken(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).userEntityToUserDto(any());
    }

    @Test
    void createUser_whenAddressesAreNull_shouldThrowValidationException() {
        // Given
        userEntity.setAddresses(null);
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(userEntity);

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.createUser(userDto));

        // Then
        assertEquals(UserServiceException.UserErrorType.VALIDATION_ERROR, exception.getErrorType());

        verify(userRepository).findByEmail(userDto.email());
        verify(userMapper).userDtoToUserEntity(userDto);

        verify(idGenerator, never()).generateUserId(anyInt());
        verify(passwordEncoder, never()).encode(anyString());
        verify(verificationTokenGenerator, never()).generateEmailVerificationToken(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).userEntityToUserDto(any());
    }

    @Test
    void createUser_whenAddressesAreEmpty_shouldThrowValidationException() {
        // Given
        userEntity.setAddresses(List.of());
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(userEntity);

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.createUser(userDto));

        // Then
        assertEquals(UserServiceException.UserErrorType.VALIDATION_ERROR, exception.getErrorType());

        verify(userRepository).findByEmail(userDto.email());
        verify(userMapper).userDtoToUserEntity(userDto);

        verify(idGenerator, never()).generateUserId(anyInt());
        verify(passwordEncoder, never()).encode(anyString());
        verify(verificationTokenGenerator, never()).generateEmailVerificationToken(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).userEntityToUserDto(any());
    }

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // Given
        userEntity.setEncryptedPassword(ENCODED_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));

        // When
        var userDetails = userService.loadUserByUsername(TEST_EMAIL);

        // Then
        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(ENCODED_PASSWORD, userDetails.getPassword());

        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUserServiceException() {
        // Given
        String unknownEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(unknownEmail)).thenReturn(Optional.empty());

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.loadUserByUsername(unknownEmail));

        // Then
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository).findByEmail(unknownEmail);
    }

    @Test
    void getUserByUserId_whenUserExists_shouldReturnUserDto() {
        // Given
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(userMapper.userEntityToUserDto(userEntity)).thenReturn(userDto);

        // When
        var user = userService.getUserByUserId(TEST_USER_ID);

        // Then
        assertNotNull(user);
        assertEquals(TEST_FIRST_NAME, user.firstName());

        verify(userRepository).findByUserId(TEST_USER_ID);
        verify(userMapper).userEntityToUserDto(userEntity);
    }

    @Test
    void getUserByUserId_whenUserDoesNotExist_shouldThrowUserServiceException() {
        // Given
        when(userRepository.findByUserId(NONEXISTENT_USER_ID)).thenReturn(Optional.empty());

        // When
        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.getUserByUserId(NONEXISTENT_USER_ID);
        });

        // Then
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository).findByUserId(NONEXISTENT_USER_ID);
        verify(userMapper, never()).userEntityToUserDto(any(UserEntity.class));
    }

    @Test
    void updateUserDto_whenUserExists_shouldReturnUpdatedUserDto() {
        // Given
        UserDto updatedInfo = new UserDto(
                null,
                null,
                "newFirstName",
                "newFamilyName",
                TEST_EMAIL,
                null,
                null,
                null,
                false,
                null);

        UserDto updatedUserDto = new UserDto(
                1L,
                TEST_USER_ID,
                "newFirstName",
                "newFamilyName",
                TEST_EMAIL,
                null,
                null,
                null,
                false,
                null);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.userEntityToUserDto(any(UserEntity.class))).thenReturn(updatedUserDto);

        // When
        UserDto updatedUser = userService.updateUserDto(TEST_USER_ID, updatedInfo);

        // Then
        assertNotNull(updatedUser);
        assertEquals("newFirstName", updatedUser.firstName());
        assertEquals("newFamilyName", updatedUser.lastName());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertEquals("newFirstName", savedUser.getFirstName());
        assertEquals("newFamilyName", savedUser.getLastName());

        verify(userRepository).findByUserId(TEST_USER_ID);
        verify(userMapper).userEntityToUserDto(savedUser);
    }

    @Test
    void updateUserDto_whenUserDoesNotExist_shouldThrowUserServiceException() {
        // Given
        UserDto updatedInfo = new UserDto(
                null,
                null,
                "newFirstName",
                "newFamilyName",
                TEST_EMAIL,
                null,
                null,
                null,
                false,
                null);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.updateUserDto(TEST_USER_ID, updatedInfo));

        // Then
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository).findByUserId(TEST_USER_ID);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).userEntityToUserDto(any(UserEntity.class));
    }

    @Test
    void deleteUser_whenUserExists_shouldDeleteUser() {
        // Given
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(userEntity));

        // When
        userService.deleteUserByUserId(TEST_USER_ID);

        // Then
        verify(userRepository).findByUserId(TEST_USER_ID);
        verify(userRepository).delete(userEntity);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldThrowUserServiceException() {
        // Given
        when(userRepository.findByUserId(NONEXISTENT_USER_ID)).thenReturn(Optional.empty());

        // When
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.deleteUserByUserId(NONEXISTENT_USER_ID));

        // Then
        assertEquals(UserServiceException.UserErrorType.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository).findByUserId(NONEXISTENT_USER_ID);
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @Test
    void getUsers_whenUsersExist_shouldReturnPageOfUserDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity), pageable, 1);
        UserDto mappedUserDto = new UserDto(
                1L,
                TEST_USER_ID,
                TEST_FIRST_NAME,
                "family1",
                TEST_EMAIL,
                null,
                null,
                null,
                false,
                null);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.userEntityToUserDto(userEntity)).thenReturn(mappedUserDto);

        // When
        Page<UserDto> users = userService.getUsers(1, 2);

        // Then
        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
        assertEquals(1, users.getContent().size());
        assertEquals(TEST_FIRST_NAME, users.getContent().get(0).firstName());

        verify(userRepository).findAll(pageable);
        verify(userMapper).userEntityToUserDto(userEntity);
    }

    @Test
    void getUsers_whenNoUsersExist_shouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> userPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDto> users = userService.getUsers(1, 2);

        // Then
        assertNotNull(users);
        assertEquals(0, users.getTotalElements());
        assertTrue(users.getContent().isEmpty());

        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).userEntityToUserDto(any(UserEntity.class));
    }

}