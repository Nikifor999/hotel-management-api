package com.back.hotelshub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ArrivalTimeDTO(
        @NotBlank
        String checkIn,
        String checkOut
) {
}
