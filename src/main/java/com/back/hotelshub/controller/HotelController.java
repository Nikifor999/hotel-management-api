package com.back.hotelshub.controller;

import com.back.hotelshub.dto.HotelDetailsDTO;
import com.back.hotelshub.dto.HotelSummaryDTO;
import com.back.hotelshub.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelSummaryDTO>> findAllHotels() {
        List<HotelSummaryDTO> list = hotelService.getAllHotels();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelDetailsDTO> findHotelById(@PathVariable Long id) {
        HotelDetailsDTO dto = hotelService.getHotelDetailsDTOById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelSummaryDTO>> searchHotels(@RequestParam String searchTerm) {}
}
