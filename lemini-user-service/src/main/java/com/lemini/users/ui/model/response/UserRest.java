package com.lemini.users.ui.model.response;

import java.util.List;

public record UserRest(
    String userId, // Public unique identifier
    String firstName,
    String lastName,
    String email,
    List<AddressRest> addresses
) {

}
