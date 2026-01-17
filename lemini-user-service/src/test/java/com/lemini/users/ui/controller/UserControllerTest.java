package com.lemini.users.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;
import com.lemini.users.shared.enums.AddressType;
import com.lemini.users.ui.mapper.UserRestMapper;
import com.lemini.users.ui.model.request.AddressRequestModel;
import com.lemini.users.ui.model.request.UserRequestModel;
import com.lemini.users.ui.model.response.UserRest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
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
            AddressType.BILLING
        );
        
        UserRequestModel requestModel = new UserRequestModel(
            "John",
            "Doe",
            "john.doe@example.com",
            "Password123!",
            List.of(address)
        );

        UserDto userDto = new UserDto(
            1L, "userId", "John", "Doe", "john.doe@example.com", "Password123!", "encryptedPass", "token", true, Collections.emptyList()
        );
        
        UserRest userRest = new UserRest(
            "userId", "John", "Doe", "john.doe@example.com", Collections.emptyList()
        );

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
            AddressType.BILLING
        );

        UserRequestModel invalidRequest = new UserRequestModel(
            "", // Empty First Name (@NotBlank)
            "", // Empty Last Name (@NotBlank)
            "invalid-email", // Invalid Email (@ValidEmail)
            "123", // Short password
            List.of(address)
        );

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
            AddressType.BILLING
        );
        
        UserRequestModel requestModel = new UserRequestModel(
            "Jane",
            "Doe",
            "existing@example.com",
            "Password123!",
            List.of(address)
        );

        UserDto userDto = new UserDto(
             0L, null, "Jane", "Doe", "existing@example.com", "Password123!", null, null, false, Collections.emptyList()
        );

        given(userRestMapper.userRequestModelToUserDto(any(UserRequestModel.class))).willReturn(userDto);
        
        // Mock service to throw exception
        given(userService.createUser(any(UserDto.class)))
            .willThrow(new UserServiceException(UserServiceException.UserErrorType.EMAIL_ALREADY_EXISTS));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isConflict());
    }
}
