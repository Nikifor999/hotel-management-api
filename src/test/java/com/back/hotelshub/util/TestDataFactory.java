package com.back.hotelshub.util;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.*;
import com.back.hotelshub.mapper.HotelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static HotelDetailsDTO createHotelDetailsDTO() {
        return HotelMapper.toDetailsDTO(createHotel());
    }

    public static Hotel createHotel() {
        Address address = Address.builder()
                .city("Minsk")
                .country("Belarus")
                .houseNumber(9)
                .id(1L)
                .postCode("220004")
                .street("Pobediteley Avenue")
                .build();

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn("14:00");
        arrivalTime.setCheckOut("12:00");

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setEmail("doubletreeminsk.info@hilton.com");
        contact.setPhone("+375 17 309-80-00");

        return Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .description("The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...")
                .address(address)
                .arrivalTime(arrivalTime)
                .amenities(createAmenities())
                .contact(contact)
                .build();
    }

    public static Set<Amenity> createAmenities() {
        return new HashSet<>(Set.of(
                Amenity.builder().id(1L).amenityName("Free parking").build(),
                Amenity.builder().id(2L).amenityName("Free WiFi").build(),
                Amenity.builder().id(3L).amenityName("Fitness center").build()
        ));
    }

    public static HotelCreationDto createHotelCreationDto() {
        AddressDTO address = AddressDTO.builder()
                .city("Minsk")
                .country("Belarus")
                .houseNumber(9)
                .postCode("220004")
                .street("Pobediteley Avenue")
                .build();

        return HotelCreationDto.builder()
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .description("The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...")
                .address(address)
                .contacts(ContactsDTO.builder()
                        .email("someemail@email.com")
                        .phone("1111111111")
                        .build())
                .arrivalTime(ArrivalTimeDTO.builder()
                        .checkIn("12:00")
                        .checkOut("14:00")
                        .build())
                .build();
    }

    public static List<HotelSummaryDTO> createHotelSummaryList() {
        HotelSummaryDTO dto1 = HotelSummaryDTO.builder()
                .id(1L)
                .name("Hotel 1")
                .description("Hotel 1 desc")
                .phone("1111111111")
                .address("Address 1")
                .build();

        HotelSummaryDTO dto2 = HotelSummaryDTO.builder()
                .id(2L)
                .name("Hotel 2")
                .description("Hotel 2 desc")
                .phone("1111111112")
                .address("Address 2")
                .build();

        return List.of(dto1, dto2);
    }
}
