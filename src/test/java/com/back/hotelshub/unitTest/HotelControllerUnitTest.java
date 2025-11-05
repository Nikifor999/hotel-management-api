package com.back.hotelshub.unitTest;

import com.back.hotelshub.controller.HotelController;
import com.back.hotelshub.dto.HotelDetailsDTO;
import com.back.hotelshub.dto.HotelSummaryDTO;
import com.back.hotelshub.entity.*;
import com.back.hotelshub.service.HotelService;
import com.back.hotelshub.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

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

    @Test
    @DisplayName("should return specific hotels  for serch criteria")
    void shouldReturnSpecificHotelsForSearchCriteria() throws Exception {

    }
}
