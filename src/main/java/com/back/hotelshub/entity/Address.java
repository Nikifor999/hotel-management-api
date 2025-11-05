package com.back.hotelshub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String city;

    @NotEmpty
    private String street;

    @Column(name = "post_code")
    private String postcode;

    @NotEmpty
    private String country;

    @Column(name = "house_number")
    @Positive
    private int houseNumber;

}
