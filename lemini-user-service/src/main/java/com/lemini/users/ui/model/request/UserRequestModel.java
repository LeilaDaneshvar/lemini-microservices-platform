package com.lemini.users.ui.model.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UserRequestModel(

    @NotNull(message = "{validation.firstName.notNull}")
    @Size(min = 2, max = 50, message = "{validation.firstName.size}")
    String firstName,

    @NotNull(message = "{validation.lastName.notNull}")
    @Size(min = 2, max = 50, message = "{validation.lastName.size}")
    String lastName,

    @NotNull(message = "{validation.email.notNull}")
    @Email(message = "{validation.email.format}")
    @Size(max = 120, message = "{validation.email.size}")
    String email,

    @NotNull(message = "{validation.password.notNull}")
    @Size(min = 8, max = 20, message = "{validation.password.size}")
    String password,

    @Valid
    @NotEmpty(message = "{validation.addresses.notEmpty}")
    List<AddressRequestModel> addresses
) {

}
