package com.back.hotelshub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Embeddable
public class ArrivalTime {
    @Column(name = "arrival_check_in")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String checkIn;

    @Column(name = "arrival_check_out")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String checkOut;
}