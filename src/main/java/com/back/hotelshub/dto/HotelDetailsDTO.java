package com.back.hotelshub.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HotelDetailsDTO(
        Long id,
        String name,
        String description,
        String brand,
        AddressDTO address,
        ContactsDTO contacts,
        ArrivalTimeDTO arrivalTime,
        List<String> amenities
) {
}
