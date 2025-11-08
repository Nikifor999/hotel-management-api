package com.back.hotelshub.mapper;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelMapper {


    public static HotelSummaryDTO toSummaryDTO(final Hotel hotel) {
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

    public static Hotel fromCreationDtoToHotel(final HotelCreationDto dto) {
        if (dto == null) {
            return null;
        }

        return Hotel.builder()
                .name(dto.name())
                .description(dto.description())
                .brand(dto.brand())
                .address(toAddressEntity(dto.address()))
                .contact(toContactEntity(dto.contacts()))
                .arrivalTime(toArrivalTimeEntity(dto.arrivalTime()))
                .build();
    }

    private static Address toAddressEntity(final AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setHouseNumber(dto.houseNumber());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setCountry(dto.country());
        address.setPostcode(dto.postcode());
        return address;
    }

    private static Contact toContactEntity(final ContactsDTO dto) {
        if (dto == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setPhone(dto.phone());
        contact.setEmail(dto.email());
        return contact;
    }

    private static ArrivalTime toArrivalTimeEntity(final ArrivalTimeDTO dto) {
        if (dto == null) {
            return null;
        }

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(dto.checkIn());
        arrivalTime.setCheckOut(dto.checkOut());
        return arrivalTime;
    }

    private static Set<Amenity> toAmenitiesEntity(final List<String> amenityNames) {
        if (amenityNames == null || amenityNames.isEmpty()) {
            return Set.of();
        }

        return amenityNames.stream()
                .map(HotelMapper::createAmenityFromName)
                .collect(Collectors.toSet());
    }

    private static Amenity createAmenityFromName(final String name) {
        Amenity amenity = new Amenity();
        amenity.setAmenityName(name);
        return amenity;
    }
}
