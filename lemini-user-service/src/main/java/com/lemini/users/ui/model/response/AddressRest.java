package com.lemini.users.ui.model.response;

import com.lemini.users.shared.enums.AddressType;

public record AddressRest(
    String addressId,
    String streetName,
    String city,
    String country,
    String postalCode,
    AddressType type
) {

}
