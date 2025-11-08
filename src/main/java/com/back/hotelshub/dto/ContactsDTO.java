package com.back.hotelshub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ContactsDTO(
        @NotBlank
        String phone,
        @Email
        String email
) {
}
