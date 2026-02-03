package com.lemini.users.ui.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;
import com.lemini.users.ui.mapper.UserRestMapper;
import com.lemini.users.ui.model.request.UpdateUserRequestModel;
import com.lemini.users.ui.model.request.UserRequestModel;
import com.lemini.users.ui.model.response.ApiErrorResponse;
import com.lemini.users.ui.model.response.UserRest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Tag(name = "User Controller", description = "API For Managing User Profiles")
public class UserController {

        private final UserService userService;
        private final UserRestMapper mapper;

        @Operation(summary = "Create User profile", description = "Create a new user with personal details and addresses")
        @ApiResponses(value = {
                        // Senario 1: Successful Creation
                        @ApiResponse(responseCode = "201", description = "User created successfully"),

                        // Senario 2: Error
                        @ApiResponse(responseCode = "400", description = "Validation Error (e.g. Invalid email format, missing fields)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),

                        @ApiResponse(responseCode = "409", description = "Conflict (Email already exists)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))

        })
        @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
                        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
        public ResponseEntity<UserRest> createUser(@Valid @RequestBody UserRequestModel userDetails) {

                // Map Request Model to DTO
                UserDto userDto = mapper.userRequestModelToUserDto(userDetails);

                // Create User
                UserDto createdUser = userService.createUser(userDto);

                // Map DTO to Response Model
                UserRest returnValue = mapper.userDtoToUserRest(createdUser);

                // Return Response
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(returnValue);
        }

        @Operation(summary = "Get User info by public Id ", description = "Retrieve user profile information using the public user ID for logged in user", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        // Senario 1: Successful Retrieval
                        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),

                        // Senario 2: Error
                        @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),

                        @ApiResponse(responseCode = "401", description = "Unauthorized (Invalid or missing authentication token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))

        })
        @GetMapping(path = "{userId}", produces = {
                        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
        public ResponseEntity<UserRest> getUser(
                        @Parameter(description = "Public user ID", example = "user123") @PathVariable("userId") String userId) {

                 // Validate userId
                if (userId == null || userId.isEmpty()) {
                        throw new IllegalArgumentException("{validation.user.id.required}");
                }

                // Retrieve User DTO
                UserDto userDto = userService.getUserByUserId(userId);

                // Map DTO to Response Model
                UserRest returnValue = mapper.userDtoToUserRest(userDto);

                // Return Response
                return ResponseEntity.status(HttpStatus.OK)
                                .body(returnValue);
        }

        @Operation(summary = "Update User info by public Id ", description = "Update user profile information using the public user ID for logged in user", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        // Senario 1: Successful Update
                        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
                        // Senario 2: Error
                        @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),

                        @ApiResponse(responseCode = "401", description = "Unauthorized (Invalid or missing authentication token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))

        })
        @PutMapping(path = "{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE }, produces = {
                                        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
        public ResponseEntity<UserRest> updateUser(
                        @Parameter(description = "Public user ID", example = "user123") @PathVariable("userId") String userId,
                        @Valid @RequestBody UpdateUserRequestModel userDetails) {

                // Validate userId
                if (userId == null || userId.isEmpty()) {
                        throw new IllegalArgumentException("{validation.user.id.required}");
                }

                // Map Request Model to DTO
                UserDto userDto = mapper.updateUserRequestModelToUserDto(userDetails);

                // Update User
                UserDto updatedUser = userService.updateUserDto(userId, userDto);

                // Map DTO to Response Model
                UserRest returnValue = mapper.updateUserDtoToUserRest(updatedUser);

                // Return Response
                return ResponseEntity.status(HttpStatus.OK)
                                .body(returnValue);
        }
}