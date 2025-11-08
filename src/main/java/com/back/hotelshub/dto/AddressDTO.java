package com.back.hotelshub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record AddressDTO(
        @Positive
        int houseNumber,
        @NotBlank
        String street,
        @NotBlank
        String city,
        @NotBlank
        String country,
        String postcode
) {
}
