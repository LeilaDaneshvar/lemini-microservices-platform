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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private UserRestMapper userRestMapper;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /users - 201 Created: Successful profile creation with valid data")
        void createUser_whenValidData_returns201() throws Exception {
                // Arrange
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
                                1L, "userId", "John", "Doe", "john.doe@example.com", "Password123!", "encryptedPass",
                                "token", true,
                                Collections.emptyList());

                UserRest userRest = new UserRest(
                                "userId", "John", "Doe", "john.doe@example.com", Collections.emptyList());

                given(userRestMapper.userRequestModelToUserDto(any(UserRequestModel.class))).willReturn(userDto);
                given(userService.createUser(any(UserDto.class))).willReturn(userDto);
                given(userRestMapper.userDtoToUserRest(any(UserDto.class))).willReturn(userRest);

                // Act & Assert
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("POST /users - 400 Bad Request: Triggered by validation failures")
        void createUser_whenInvalidData_returns400() throws Exception {
                // Arrange - Invalid Email and missing names
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

                // Act & Assert
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /users - 409 Conflict: Attempt to register existing email")
        void createUser_whenEmailExists_returns409() throws Exception {
                // Arrange
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
                given(userService.createUser(any(UserDto.class)))
                                .willThrow(new UserServiceException(
                                                UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS));

                // Act & Assert
                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Get /users/{userId} - 200 OK: Successful retrieval of user profile by userId")
        void getUser_whenUserIdExists_returns200() throws Exception {
                // Arrange
                String userId = "existing-user-id";
                UserDto userDto = new UserDto(
                                1L, userId, "Alice", "Smith", "alice.smith@example.com", "Password123!",
                                "encryptedPass", "token", true,
                                Collections.emptyList());
                UserRest userRest = new UserRest(
                                userId, "Alice", "Smith", "alice.smith@example.com", Collections.emptyList());
                given(userService.getUserByUserId(userId)).willReturn(userDto);
                given(userRestMapper.userDtoToUserRest(userDto)).willReturn(userRest);
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/v1/users/{userId}", userId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get /users/{userId} - 404 Not Found: UserId does not exist")
        void getUser_whenUserIdNotExists_returns404() throws Exception {
                // Arrange
                String userId = "nonexistent-user-id";
                given(userService.getUserByUserId(userId))
                                .willThrow(new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/v1/users/{userId}", userId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Put /users/{userId} - 200 OK : Successful update of user profile")
        void updateUser_whenValidData_returns200() throws Exception {
                // Arrange
                String userId = "existing-user-id";
                AddressRequestModel address = new AddressRequestModel(
                                "Los Angeles",
                                "USA",
                                "Sunset Boulevard",
                                "90001",
                                AddressType.SHIPPING);
                UserRequestModel requestModel = new UserRequestModel(
                                "Bob",
                                "Johnson",
                                "bob.johnson@example.com",
                                "Password123!",
                                List.of(address));

                UserDto userDto = new UserDto(
                                1L, userId, "Bob", "Johnson", "bob.johnson@example.com", "Password123!",
                                "encryptedPass", "token", true,
                                List.of());
                UserRest userRest = new UserRest(
                                userId, "Bob", "Johnson", "bob.johnson@example.com", Collections.emptyList());
                given(userRestMapper.userRequestModelToUserDto(any(UserRequestModel.class))).willReturn(userDto);
                given(userService.updateUserDto(any(String.class), any(UserDto.class))).willReturn(userDto);
                given(userRestMapper.userDtoToUserRest(any(UserDto.class))).willReturn(userRest);
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/api/v1/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Put /users/{userId} - 404 Not Found: Attempt to update non-existent user")
        void updateUser_whenUserIdNotExists_returns404() throws Exception {
                // Arrange
                String userId = "nonexistent-user-id";
                UpdateUserRequestModel requestModel = new UpdateUserRequestModel(
                                "Bob",
                                "Johnson");
                UserDto userDto = new UserDto(
                                0L, "userId", "Bob", "Johnson", "bob.johnson@example.com", "Password123!",
                                "encryptedPass", "token",
                                true,
                                List.of());
                given(userRestMapper.updateUserRequestModelToUserDto(any(UpdateUserRequestModel.class)))
                                .willReturn(userDto);
                given(userService.updateUserDto(any(String.class), any(UserDto.class)))
                                .willThrow(new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND));
                // Act & Assert
                mockMvc.perform(MockMvcRequestBuilders
                                .put("/api/v1/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestModel)))
                                .andExpect(status().isNotFound());

        }

        @Test
        @DisplayName("Put /users/{userId} - 400 Bad Request: Attempt to update non-existent user")
        void updateUser_whenInvalidData_returns400() throws Exception {
                // Arrange
                String userId = "existing-user-id";
                UpdateUserRequestModel invalidRequest = new UpdateUserRequestModel(
                                "", // Empty First Name (@NotBlank)
                                ""); // Empty Last Name (@NotBlank)

                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/api/v1/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Delete /users/{userId} - 200 OK: Successful deletion of user profile")
        void deleteUser_whenUserIdExists_returns200() throws Exception {
                // Arrange
                String userId = "existing-user-id";
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .delete("/api/v1/users/{userId}", userId))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Delete /users/{userId} - 404 Not Found: UserId does not exist")
        void deleteUser_whenUserIdNotExists_returns404() throws Exception {
                // Arrange
                String userId = "nonexistent-user-id";
                Mockito.doThrow(new UserServiceException(UserServiceException.UserErrorType.USER_NOT_FOUND))
                                .when(userService).deleteUserByUserId(userId);
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .delete("/api/v1/users/{userId}", userId))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Delete /users/{userId} - 400 Bad Request: Triggered by validation failures")
        void deleteUser_whenInvalidUserId_returns400() throws Exception {
                // Arrange
                String invalidUserId = ""; // Empty userId

                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .delete("/api/v1/users/{userId}", invalidUserId))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Get /users - 200 OK: Successful retrieval of all user profiles")
        void getAllUsers_whenUsersExist_returns200() throws Exception {
                // Arrange
                UserDto userDto = new UserDto(
                                1L, "userId", "Charlie", "Brown", "charlie.brown@example.com",
                                "password123", "encPass", "token123", false, List.of());
                UserRest userRest = new UserRest(
                                "userId", "Charlie", "Brown", "charlie.brown@example.com", List.of());
                given(userService.getUsers(10,10)).willReturn(List.of(userDto));
                given(userRestMapper.userDtoToUserRest(any(UserDto.class))).willReturn(userRest);
                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/v1/users")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                }

        @Test
        @DisplayName("Get /users - 400 Bad Request: Triggered by validation failures")
        void getAllUsers_whenInvalidPagination_returns400() throws Exception {
                // Arrange
                int invalidPage = -1; // Negative page number
                int invalidLimit = 0; // Zero limit

                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/v1/users")
                                .param("page", String.valueOf(invalidPage))
                                .param("limit", String.valueOf(invalidLimit))
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }       

        


}
