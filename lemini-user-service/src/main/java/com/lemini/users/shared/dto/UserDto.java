package com.lemini.users.shared.dto;

import java.io.Serializable;
import java.util.List;

public record UserDto(
    Long id,
    String userId,
    String firstName,
    String lastName,
    String email,
    String password,
    String encryptedPassword,
    String emailVerificationToken,
    Boolean emailVerificationStatus,
    List<AddressDto> addresses
) implements Serializable {
} 