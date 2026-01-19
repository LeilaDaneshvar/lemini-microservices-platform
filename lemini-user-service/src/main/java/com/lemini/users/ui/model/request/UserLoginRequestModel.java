package com.lemini.users.ui.model.request;

import com.lemini.users.validation.ValidEmail;
import com.lemini.users.validation.ValidPassword;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequestModel(

    @NotNull(message = "{validation.email.notNull}")
    @ValidEmail(message = "{validation.email.format}")
    @Size(max = 120, message = "{validation.email.size}")
    String email,
    
    @NotNull(message = "{validation.password.notNull}")
    @Size(min = 8, max = 20, message = "{validation.password.size}")
    @ValidPassword(message = "{validation.password.format}")
    String password) {
} 