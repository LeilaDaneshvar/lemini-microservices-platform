package com.lemini.users.ui.model.request;

import com.lemini.users.validation.ValidEmail;
import com.lemini.users.validation.ValidPassword;
import com.lemini.users.validation.ValidationConstants;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequestModel(

    @NotNull(message = "{validation.email.notNull}")
    @Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "{validation.email.size}")
    @ValidEmail(message = "{validation.email.format}")
    String email,
    
    @NotNull(message = "{validation.password.notNull}")
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "{validation.password.size}")
    @ValidPassword(message = "{validation.password.format}")
    String password) {
} 