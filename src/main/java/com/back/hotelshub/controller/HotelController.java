package com.back.hotelshub.controller;

import com.back.hotelshub.dto.HotelCreationDto;
import com.back.hotelshub.dto.HotelDetailsDTO;
import com.back.hotelshub.dto.HotelSearchRequest;
import com.back.hotelshub.dto.HotelSummaryDTO;
import com.back.hotelshub.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
public class HotelController implements HotelApi{

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
    public ResponseEntity<List<HotelSummaryDTO>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities // supports ?amenities=a&amenities=b
    ) {
        List<String> amenityNames = parseAmenitiesParam(amenities);
        HotelSearchRequest request = new HotelSearchRequest(
                name, brand, city, country, amenityNames);
        List<HotelSummaryDTO> list = hotelService.search(request);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelSummaryDTO> createHotel(@Validated @RequestBody HotelCreationDto dto) {
        HotelSummaryDTO response = hotelService.createHotel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<Void> addAmenitiesToHotel(@PathVariable Long id,
                                                    @Validated @RequestBody List<String> amenities) {
        hotelService.addAmenitiesToHotelById(id, amenities);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/histogram/{param}")
    public ResponseEntity< Map<String, Long>> getHistogramByParam(@PathVariable String param) {

        Map<String, Long> result = hotelService.getHistogramByParam(param);
        return ResponseEntity.ok().body(result);
    }


    private List<String> parseAmenitiesParam(List<String> amenities) {
        if (amenities == null) return Collections.emptyList();
        return amenities.stream()
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
