package com.back.hotelshub.mapper;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelMapper {


    public static HotelSummaryDTO ToDTO(final Hotel hotel) {
        String fullAddress = null;
        if (hotel.getAddress() != null) {
            Address a = hotel.getAddress();
            fullAddress = String.format("%d %s, %s, %s, %s",
                    a.getHouseNumber(),
                    a.getStreet(),
                    a.getCity(),
                    a.getPostcode(),
                    a.getCountry());
        }

        String phone = (hotel.getContact() != null) ? hotel.getContact().getPhone() : "Unknown";

        return HotelSummaryDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .phone(phone)
                .address(fullAddress)
                .build();
    }

    public static HotelDetailsDTO toDetailsDTO(Hotel hotel) {
        if (hotel == null) return null;

        return HotelDetailsDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .brand(hotel.getBrand())
                .address(toAddressDTO(hotel.getAddress()))
                .contacts(toContactsDTO(hotel.getContact()))
                .arrivalTime(toArrivalTimeDTO(hotel.getArrivalTime()))
                .amenities(toAmenitiesList(hotel.getAmenities()))
                .build();
    }

    private static AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;
        return new AddressDTO(
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getPostcode()
        );
    }

    private static ContactsDTO toContactsDTO(Contact contact) {
        if (contact == null) return new ContactsDTO(
                "Unknown",
                "Unknown"
        );
        ;
        return new ContactsDTO(
                contact.getPhone(),
                contact.getEmail()
        );
    }

    private static ArrivalTimeDTO toArrivalTimeDTO(ArrivalTime arrivalTime) {
        if (arrivalTime == null) return new ArrivalTimeDTO(
                "Unknown",
                "Unknown"
        );
        return new ArrivalTimeDTO(
                arrivalTime.getCheckIn(),
                arrivalTime.getCheckOut()
        );
    }

    private static List<String> toAmenitiesList(Set<Amenity> amenities) {
        if (amenities == null) return List.of();
        return amenities.stream()
                .map(Amenity::getAmenityName)
                .collect(Collectors.toList());
    }
}
