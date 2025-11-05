package com.back.hotelshub.dto;

import lombok.Builder;

@Builder
public record HotelSummaryDTO(
        Long id,
        String name,
        String description,
        String address,
        String phone
) {
}
