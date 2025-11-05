package com.back.hotelshub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name = "name")
    private String amenityName;

    @ManyToMany(mappedBy = "amenities")
    private Set<Hotel> hotels = new HashSet<>();
}
