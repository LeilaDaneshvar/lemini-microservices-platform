package com.lemini.users.shared.dto;

import java.io.Serializable;

public record AddressDto(
    long id,
    String addressId,
    String streetName,
    String city,
    String country,
    String postalCode,
    String type) implements Serializable{
} 
