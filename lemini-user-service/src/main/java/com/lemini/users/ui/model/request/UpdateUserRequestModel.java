package com.lemini.users.ui.model.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;


public record UpdateUserRequestModel(

    @NotBlank(message = "{validation.firstName.notNull}")
    @Size(min = 2, max = 50, message = "{validation.firstName.size}")
    String firstName,

    @NotBlank(message = "{validation.lastName.notNull}")
    @Size(min = 2, max = 50, message = "{validation.lastName.size}")
    String lastName
) {

}
