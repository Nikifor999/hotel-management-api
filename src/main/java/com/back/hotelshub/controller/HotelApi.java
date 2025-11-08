package com.back.hotelshub.controller;

import com.back.hotelshub.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Hotels", description = "Operations related to hotels management")
@RequestMapping("/property-view")
public interface HotelApi {

    @Operation(summary = "Get all hotels (summary view)",
            description = "Returns a list of all hotels with brief information.")
    @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of hotels list",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelSummaryDTO.class)))
    )
    @GetMapping("/hotels")
    ResponseEntity<List<HotelSummaryDTO>> findAllHotels();


    @Operation(summary = "Get hotel details by ID",
            description = "Returns detailed information about a specific hotel.")
    @ApiResponse(responseCode = "200", description = "Hotel details found",
            content = @Content(schema = @Schema(implementation = HotelDetailsDTO.class)))
    @ApiResponse(responseCode = "404", description = "Hotel not found")
    @GetMapping("/hotels/{id}")
    ResponseEntity<HotelDetailsDTO> findHotelById(@PathVariable Long id);


    @Operation(summary = "Search hotels by filters",
            description = """
                    Allows searching hotels by optional parameters:
                    name, brand, city, country, and amenities.
                    Multiple amenities can be provided as ?amenities=a&amenities=b
                    """)
    @ApiResponse(responseCode = "200", description = "Successful search result",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelSummaryDTO.class))))
    @GetMapping("/search")
    ResponseEntity<List<HotelSummaryDTO>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities
    );


    @Operation(summary = "Create new hotel",
            description = "Creates a new hotel entry with address, contacts, and optional amenities.")
    @ApiResponse(responseCode = "201", description = "Hotel successfully created",
            content = @Content(schema = @Schema(implementation = HotelSummaryDTO.class)))
    @PostMapping("/hotels")
    ResponseEntity<HotelSummaryDTO> createHotel(@Validated @RequestBody HotelCreationDto dto);


    @Operation(summary = "Add amenities to hotel",
            description = "Adds a list of amenities to an existing hotel.")
    @ApiResponse(responseCode = "201", description = "Amenities added successfully")
    @PostMapping("/hotels/{id}/amenities")
    ResponseEntity<Void> addAmenitiesToHotel(@PathVariable Long id,
                                             @Validated @RequestBody List<String> amenities);


    @Operation(summary = "Get histogram of hotels by parameter",
            description = "Groups hotels by brand, city, country, or amenities and returns counts per group.")
    @ApiResponse(responseCode = "200", description = "Histogram data retrieved successfully",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/histogram/{param}")
    ResponseEntity<Map<String, Long>> getHistogramByParam(@PathVariable String param);
}
