package com.lemini.users.ui.model.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import com.lemini.users.validation.ValidEmail;
import com.lemini.users.validation.ValidPassword;
import com.lemini.users.validation.ValidationConstants;

import java.util.List;

public record UserRequestModel(

    @NotBlank(message = "{validation.firstName.notNull}")
    @Size(min = ValidationConstants.FIRST_NAME_MIN_LENGTH, max = ValidationConstants.FIRST_NAME_MAX_LENGTH, message = "{validation.firstName.size}")
    String firstName,

    @NotBlank(message = "{validation.lastName.notNull}")
    @Size(min = ValidationConstants.LAST_NAME_MIN_LENGTH, max = ValidationConstants.LAST_NAME_MAX_LENGTH, message = "{validation.lastName.size}")
    String lastName,

    @NotNull(message = "{validation.email.notNull}")
    @ValidEmail(message = "{validation.email.format}")
    @Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "{validation.email.size}")
    String email,

    @NotNull(message = "{validation.password.notNull}")
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "{validation.password.size}")
    @ValidPassword(message = "{validation.password.format}")
    String password,

    @Valid
    @NotEmpty(message = "{validation.addresses.notEmpty}")
    List<AddressRequestModel> addresses
) {

}
