package com.back.hotelshub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(
            regexp = "^\\+?[\\d\\s\\-\\(\\)]{7,20}$",
            message = "Phone number can contain digits, spaces, hyphens and parentheses"
    )
    private String phone;

    @Email(message = "Invalid email format")
    private String email;
}
