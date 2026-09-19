package com.lemini.users.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemini.users.exceptions.GlobalExceptionHandler;
import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;
import com.lemini.users.shared.enums.AddressType;
import com.lemini.users.ui.mapper.UserRestMapper;
import com.lemini.users.ui.model.request.AddressRequestModel;
import com.lemini.users.ui.model.request.UpdateUserRequestModel;
import com.lemini.users.ui.model.request.UserRequestModel;
import com.lemini.users.ui.model.response.UserRest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private UserRestMapper userRestMapper;

        @Autowired
        private ObjectMapper objectMapper;

        private static final String BASE_URL = "/api/v1/users";

        @Test
        @DisplayName("POST /users - 201 Created: Successful profile creation with valid data")
        void createUser_whenValidData_returns201() throws Exception {
                // Given
                AddressRequestModel address = new AddressRequestModel(
                                "New York",
                                "USA",
                                "5th Avenue",
                                "10001",
                                AddressType.BILLING);

                UserRequestModel requestModel = new UserRequestModel(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                "Password123!",
                                List.of(address));

                UserDto userDto = new UserDto(
                                1L,
                                "userId",
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                "Password123!",
                                "encryptedPass",
                                "token",
                                true,
                                Collections.emptyList());

                UserRest userRest = new UserRest(
                                "userId",
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                Collections.emptyList());

                given(userRestMapper.userRequestModelToUserDto(any(UserRequestModel.class)))
                                .willReturn(userDto);

                given(userService.createUser(userDto))
                                .willReturn(userDto);

                given(userRestMapper.userDtoToUserRest(userDto))
                                .willReturn(userRest);

                // When & Then
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId").value("userId"))
                                .andExpect(jsonPath("$.firstName").value("John"))
                                .andExpect(jsonPath("$.lastName").value("Doe"))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                                .andExpect(jsonPath("$.password").doesNotExist())
                                .andExpect(jsonPath("$.encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.emailVerificationToken").doesNotExist());

                verify(userService).createUser(userDto);
                verify(userRestMapper).userDtoToUserRest(userDto);
        }

        @Test
        @DisplayName("POST /users - 400 Bad Request: Triggered by validation failures")
        void createUser_whenInvalidData_returns400() throws Exception {
                // Given
                AddressRequestModel address = new AddressRequestModel(
                                "New York",
                                "USA",
                                "5th Avenue",
                                "10001",
                                AddressType.BILLING);

                UserRequestModel invalidRequest = new UserRequestModel(
                                "", // Empty First Name (@NotBlank)
                                "", // Empty Last Name (@NotBlank)
                                "invalid-email", // Invalid Email (@ValidEmail)
                                "123", // Short password
                                List.of(address));

                // When & Then
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(userRestMapper, never()).userDtoToUserRest(any(UserDto.class));
                verify(userService, never()).createUser(any(UserDto.class));
                verify(userRestMapper, never()).userRequestModelToUserDto(any(UserRequestModel.class));
        }

        @Test
        @DisplayName("POST /users - 409 Conflict: Attempt to register existing email")
        void createUser_whenEmailExists_returns409() throws Exception {
                // Given
                AddressRequestModel address = new AddressRequestModel(
                                "New York",
                                "USA",
                                "5th Avenue",
                                "10001",
                                AddressType.BILLING);

                UserRequestModel requestModel = new UserRequestModel(
                                "Jane",
                                "Doe",
                                "existing@example.com",
                                "Password123!",
                                List.of(address));

                UserDto userDto = new UserDto(
                                0L, null, "Jane", "Doe", "existing@example.com", "Password123!", null, null, false,
                                Collections.emptyList());

                given(userRestMapper.userRequestModelToUserDto(any(UserRequestModel.class))).willReturn(userDto);

                // Mock service to throw exception
                given(userService.createUser(userDto))
                                .willThrow(new UserServiceException(
                                                UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS));

                // When & Then
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isConflict());

                verify(userService).createUser(userDto);
                verify(userRestMapper).userRequestModelToUserDto(any(UserRequestModel.class));
                verify(userRestMapper, never()).userDtoToUserRest(any(UserDto.class));
        }

        @Test
        @DisplayName("GET /users/{userId} - 200 OK: Successful retrieval of user profile by userId")
        void getUser_whenUserIdExists_returns200() throws Exception {

                // Given
                String userId = "existing-user-id";

                UserDto userDto = new UserDto(
                                1L,
                                userId,
                                "Alice",
                                "Smith",
                                "alice.smith@example.com",
                                "Password123!",
                                "encryptedPass",
                                "token",
                                true,
                                Collections.emptyList());

                UserRest userRest = new UserRest(
                                userId,
                                "Alice",
                                "Smith",
                                "alice.smith@example.com",
                                Collections.emptyList());

                given(userService.getUserByUserId(userId)).willReturn(userDto);
                given(userRestMapper.userDtoToUserRest(userDto)).willReturn(userRest);

                // When & Then
                mockMvc.perform(get(BASE_URL + "/{userId}", userId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(userId))
                                .andExpect(jsonPath("$.firstName").value("Alice"))
                                .andExpect(jsonPath("$.lastName").value("Smith"))
                                .andExpect(jsonPath("$.email").value("alice.smith@example.com"))
                                .andExpect(jsonPath("$.addresses").isArray())
                                .andExpect(jsonPath("$.password").doesNotExist())
                                .andExpect(jsonPath("$.encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.emailVerificationToken").doesNotExist());

                verify(userService).getUserByUserId(userId);
                verify(userRestMapper).userDtoToUserRest(userDto);
        }

        @Test
        @DisplayName("GET /users/{userId} - 404 Not Found: UserId does not exist")
        void getUser_whenUserIdNotExists_returns404() throws Exception {

                // Given
                String userId = "nonexistent-user-id";

                given(userService.getUserByUserId(userId))
                                .willThrow(new UserServiceException(
                                                UserServiceException.UserErrorType.USER_NOT_FOUND));

                // When & Then
                mockMvc.perform(get(BASE_URL + "/{userId}", userId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(userService).getUserByUserId(userId);
                verify(userRestMapper, never()).userDtoToUserRest(any(UserDto.class));
        }

        @Test
        @DisplayName("PUT /users/{userId} - 200 OK: Successful update of user profile")
        void updateUser_whenValidData_returns200() throws Exception {

                // Given
                String userId = "existing-user-id";

                UpdateUserRequestModel requestModel = new UpdateUserRequestModel(
                                "Bob",
                                "Johnson");

                UserDto userDto = new UserDto(
                                1L,
                                userId,
                                "Bob",
                                "Johnson",
                                "bob.johnson@example.com",
                                "Password123!",
                                "encryptedPass",
                                "token",
                                true,
                                List.of());

                UserRest userRest = new UserRest(
                                userId,
                                "Bob",
                                "Johnson",
                                "bob.johnson@example.com",
                                Collections.emptyList());

                given(userRestMapper.updateUserRequestModelToUserDto(any(UpdateUserRequestModel.class)))
                                .willReturn(userDto);
                given(userService.updateUserDto(userId, userDto)).willReturn(userDto);
                given(userRestMapper.updateUserDtoToUserRest(userDto)).willReturn(userRest);

                // When & Then
                mockMvc.perform(put(BASE_URL + "/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(userId))
                                .andExpect(jsonPath("$.firstName").value("Bob"))
                                .andExpect(jsonPath("$.lastName").value("Johnson"))
                                .andExpect(jsonPath("$.email").value("bob.johnson@example.com"))
                                .andExpect(jsonPath("$.password").doesNotExist())
                                .andExpect(jsonPath("$.encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.emailVerificationToken").doesNotExist());

                verify(userRestMapper).updateUserRequestModelToUserDto(any(UpdateUserRequestModel.class));
                verify(userService).updateUserDto(userId, userDto);
                verify(userRestMapper).updateUserDtoToUserRest(userDto);
        }

        @Test
        @DisplayName("PUT /users/{userId} - 404 Not Found: Attempt to update non-existent user")
        void updateUser_whenUserIdDoesNotExist_returns404() throws Exception {

                // Given
                String userId = "nonexistent-user-id";

                UpdateUserRequestModel requestModel = new UpdateUserRequestModel(
                                "Bob",
                                "Johnson");

                UserDto userDto = new UserDto(
                                null,
                                userId,
                                "Bob",
                                "Johnson",
                                null,
                                null,
                                null,
                                null,
                                false,
                                null);

                given(userRestMapper.updateUserRequestModelToUserDto(
                                any(UpdateUserRequestModel.class)))
                                .willReturn(userDto);

                given(userService.updateUserDto(userId, userDto))
                                .willThrow(new UserServiceException(
                                                UserServiceException.UserErrorType.USER_NOT_FOUND));

                // When & Then
                mockMvc.perform(put(BASE_URL + "/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isNotFound());

                verify(userRestMapper).updateUserRequestModelToUserDto(any(UpdateUserRequestModel.class));
                verify(userService).updateUserDto(userId, userDto);
                verify(userRestMapper, never()).updateUserDtoToUserRest(any(UserDto.class));
        }

        @Test
        @DisplayName("PUT /users/{userId} - 400 Bad Request: Invalid update data")
        void updateUser_whenInvalidData_returns400() throws Exception {

                // Given
                String userId = "existing-user-id";

                UpdateUserRequestModel invalidRequest = new UpdateUserRequestModel(
                                "", // Empty First Name (@NotBlank)
                                ""); // Empty Last Name (@NotBlank)

                // When & Then
                mockMvc.perform(put(BASE_URL + "/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(userService, userRestMapper);
        }

        @Test
        @DisplayName("DELETE /users/{userId} - 200 OK: Successful deletion of user profile")
        void deleteUser_whenUserIdExists_returns200() throws Exception {

                // Given
                String userId = "existing-user-id";

                // When & Then
                mockMvc.perform(delete(BASE_URL + "/{userId}", userId))
                                .andExpect(status().isOk());

                verify(userService).deleteUserByUserId(userId);
        }

        @Test
        @DisplayName("DELETE /users/{userId} - 404 Not Found: UserId does not exist")
        void deleteUser_whenUserIdDoesNotExist_returns404() throws Exception {

                // Given
                String userId = "nonexistent-user-id";

                doThrow(new UserServiceException(
                                UserServiceException.UserErrorType.USER_NOT_FOUND))
                                .when(userService)
                                .deleteUserByUserId(userId);

                // When & Then
                mockMvc.perform(delete(BASE_URL + "/{userId}", userId))
                                .andExpect(status().isNotFound());

                verify(userService).deleteUserByUserId(userId);
        }

        @Test
        @DisplayName("GET /users - 200 OK: Successful retrieval of users")
        void getAllUsers_whenUsersExist_returns200() throws Exception {

                // Given
                UserDto userDto = new UserDto(
                                1L,
                                "userId",
                                "Charlie",
                                "Brown",
                                "charlie.brown@example.com",
                                "password123",
                                "encPass",
                                "token123",
                                false,
                                List.of());

                UserRest userRest = new UserRest(
                                "userId",
                                "Charlie",
                                "Brown",
                                "charlie.brown@example.com",
                                List.of());

                Page<UserDto> userDtoPage = new PageImpl<>(List.of(userDto));

                given(userService.getUsers(1, 10))
                                .willReturn(userDtoPage);

                given(userRestMapper.userDtoToUserRest(userDto))
                                .willReturn(userRest);

                // When & Then
                mockMvc.perform(get(BASE_URL)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.users").isArray())
                                .andExpect(jsonPath("$.users[0].userId").value("userId"))
                                .andExpect(jsonPath("$.users[0].firstName").value("Charlie"))
                                .andExpect(jsonPath("$.users[0].lastName").value("Brown"))
                                .andExpect(jsonPath("$.users[0].email")
                                                .value("charlie.brown@example.com"))
                                .andExpect(jsonPath("$.users[0].password").doesNotExist())
                                .andExpect(jsonPath("$.users[0].encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.users[0].emailVerificationToken").doesNotExist());

                verify(userService).getUsers(1, 10);
                verify(userRestMapper).userDtoToUserRest(userDto);
        }

        @Test
        @DisplayName("Get /users - 400 Bad Request: Triggered by validation failures")
        void getAllUsers_whenInvalidPagination_returns400() throws Exception {
                mockMvc.perform(get(BASE_URL)
                                .param("page", "0")
                                .param("limit", "10")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(userService);
        }

}
