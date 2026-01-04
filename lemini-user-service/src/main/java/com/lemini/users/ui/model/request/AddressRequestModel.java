package com.lemini.users.ui.model.request;

import com.lemini.users.shared.enums.AddressType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequestModel(

    @NotNull(message = "{validation.address.city.notNull}")
    @Size(min = 2, max = 50, message = "{validation.address.city.size}")
    String city,

    @NotNull(message = "{validation.address.country.notNull}")
    @Size(min = 2, max = 50, message = "{validation.address.country.size}")
    String country,

    @NotNull(message = "{validation.address.street.notNull}")
    @Size(min = 2, max = 100, message = "{validation.address.street.size}")
    String streetName,
    
    @NotNull(message = "{validation.address.postalCode.notNull}")
    @Size(min = 3, max = 10, message = "{validation.address.postalCode.size}")
    String postalCode,
    
    @NotNull(message = "{validation.address.type.notNull}")
    AddressType type
) {

}
