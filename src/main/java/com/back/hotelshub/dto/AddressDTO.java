package com.back.hotelshub.dto;

public record AddressDTO(
        Integer houseNumber,
        String street,
        String city,
        String country,
        String postCode
) {
}
