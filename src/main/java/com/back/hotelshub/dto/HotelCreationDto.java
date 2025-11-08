package com.back.hotelshub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record HotelCreationDto(
        @NotBlank
        String name,
        String description,
        @NotBlank
        String brand,
        @Valid
        @NotNull
        AddressDTO address,
        @Valid
        @NotNull
        ContactsDTO contacts,
        @Valid
        ArrivalTimeDTO arrivalTime
) {
}
