package com.back.hotelshub.unitTest;

import com.back.hotelshub.controller.HotelController;
import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.*;
import com.back.hotelshub.mapper.HotelMapper;
import com.back.hotelshub.service.HotelService;
import com.back.hotelshub.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
public class HotelControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

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
        when(hotelService.search(any())).thenReturn(hotelDTOs);

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

        mockMvc.perform(get(BASE_URL + "/search")
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

    @Test
    @DisplayName("Should create hotel")
    void shouldCreateHotel() throws Exception {
        HotelCreationDto dto = TestDataFactory.createHotelCreationDto();
        Hotel hotel = HotelMapper.fromCreationDtoToHotel(dto);
        hotel.setId(999L);
        HotelSummaryDTO response = HotelMapper.toSummaryDTO(hotel);
        String city = dto.address().city();
        String pattern = String.format(".*%s.*", city);

        String jsonContent = objectMapper.writeValueAsString(dto);
        when(hotelService.createHotel(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/hotels")
                        .content(jsonContent)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.address").value(matchesPattern(pattern)))
                .andExpect(jsonPath("$.phone").value(dto.contacts().phone()));

        verify(hotelService, times(1)).createHotel(any());
    }


    @Test
    @DisplayName("Should return 400 when name or brand is missing")
    void shouldFailValidationWhenNameOrBrandMissing() throws Exception {
        HotelCreationDto invalidDto = HotelCreationDto.builder()
                .name("") // invalid
                .description("No name")
                .brand("") // invalid
                .address(new AddressDTO(9, "Street", "City", "Country", "12345"))
                .contacts(new ContactsDTO("+123456789", "test@mail.com"))
                .arrivalTime(new ArrivalTimeDTO("14:00", "12:00"))
                .build();

        mockMvc.perform(post(BASE_URL + "/hotels")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(hotelService, never()).createHotel(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"brand", "city", "country", "amenities"})
    @DisplayName("returning histograms for different params")
    void shouldReturnHistogramsWhenDifferentParams(String param) throws Exception {
        Map<String, Long> result = Map.of(
                param + "1", 9L,
                param + "2", 3L,
                param + "3", 7L
        );
        when(hotelService.getHistogramByParam(param)).thenReturn(result);

        mockMvc.perform(get(BASE_URL + "/histogram/{param}", param)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(hotelService, times(1)).getHistogramByParam(param);
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
