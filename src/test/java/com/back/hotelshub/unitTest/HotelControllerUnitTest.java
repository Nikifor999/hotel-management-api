package com.back.hotelshub.unitTest;

import com.back.hotelshub.controller.HotelController;
import com.back.hotelshub.dto.HotelDetailsDTO;
import com.back.hotelshub.dto.HotelSearchRequest;
import com.back.hotelshub.dto.HotelSummaryDTO;
import com.back.hotelshub.entity.*;
import com.back.hotelshub.service.HotelService;
import com.back.hotelshub.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
public class HotelControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    private static final String BASE_URL = "/property-view";

    @Test
    @DisplayName("should return all hotels DTOs")
    void shouldReturnAllHotelsDTOs() throws Exception {
        List<HotelSummaryDTO> hotelDTOs = TestDataFactory.createHotelSummaryList();

        when(hotelService.getAllHotels()).thenReturn(hotelDTOs);

        mockMvc.perform(get(BASE_URL + "/hotels")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Hotel 1"))
                .andExpect(jsonPath("$[0].address").value("Address 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].phone").value("1111111112"));

        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    @DisplayName("получение расширенной информации по конктретному отелю")
    void shouldReturnHotel() throws Exception {
        HotelDetailsDTO hotel = TestDataFactory.createHotelDetailsDTO();

        when(hotelService.getHotelDetailsDTOById(hotel.id())).thenReturn(hotel);

        mockMvc.perform(get(BASE_URL + "/hotels/" + hotel.id())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(hotel.name()))
                .andExpect(jsonPath("$.address").isNotEmpty())
                .andExpect(jsonPath("$.contacts").isNotEmpty())
                .andExpect(jsonPath("$.amenities", hasSize(hotel.amenities().size())))
                .andExpect(jsonPath("$.address.city").value(hotel.address().city()))
                .andExpect(jsonPath("$.contacts.phone").value(hotel.contacts().phone()));

        verify(hotelService, times(1)).getHotelDetailsDTOById(hotel.id());
    }

    @ParameterizedTest
    @MethodSource("hotelSearchRequests")
    @DisplayName("should return specific hotels  for search criteria")
    void shouldReturnSpecificHotelsForSearchCriteria(
            HotelSearchRequest request, String search
    ) throws Exception {

        List<HotelSummaryDTO> hotelDTOs = TestDataFactory.createHotelSummaryList();
        when(hotelService.search(any(HotelSearchRequest.class))).thenReturn(hotelDTOs);

        String searchCriteria = "/search" + search;

        mockMvc.perform(get(BASE_URL + searchCriteria)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Hotel 1"))
                .andExpect(jsonPath("$[0].address").value("Address 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].phone").value("1111111112"));

        verify(hotelService, times(1)).search(request);
    }

    @Test
    @DisplayName("should return empty list when no results found")
    void shouldReturnEmptyListWhenNoResults() throws Exception {

        when(hotelService.search(any())).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL+"/search")
                        .param("city", "Atlantis")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(hotelService, times(1)).search(any());
    }

    @Test
    @DisplayName("should return filtered hotels by brand and amenities")
    void shouldReturnHotelsFilteredByBrandAndAmenities() throws Exception {

        List<HotelSummaryDTO> hotels = List.of(
                HotelSummaryDTO.builder()
                        .id(2L)
                        .name("Hilton Moscow")
                        .description("Luxury stay in the heart of Moscow")
                        .phone("+7 495 123-45-67")
                        .address("1 Tverskaya Street, Moscow, Russia")
                        .build()
        );

        when(hotelService.search(any())).thenReturn(hotels);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("brand", "Hilton")
                        .param("amenities", "Free WiFi")
                        .param("amenities", "Fitness center")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Hilton Moscow"))
                .andExpect(jsonPath("$[0].address").value(matchesPattern(".*Moscow.*")))
                .andExpect(jsonPath("$[0].phone").value("+7 495 123-45-67"));

        verify(hotelService, times(1)).search(any());
    }

    public static Stream<Arguments> hotelSearchRequests() {
        return Stream.of(
                Arguments.of(new HotelSearchRequest(
                        "Hilton", null, null, null, new ArrayList<>()
                ), "?name=Hilton"),
                Arguments.of(new HotelSearchRequest(
                        null, "Hilton", null, null, new ArrayList<>()
                ), "?brand=Hilton"),
                Arguments.of(new HotelSearchRequest(
                        null, null, "Minsk", null, new ArrayList<>()
                ), "?city=Minsk")
        );
    }
}
