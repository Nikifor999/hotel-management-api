package com.back.hotelshub.service;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.Hotel;
import com.back.hotelshub.mapper.HotelMapper;
import com.back.hotelshub.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelSummaryDTO> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(HotelMapper::toSummaryDTO)
                .toList();
    }

    public HotelDetailsDTO getHotelDetailsDTOById(Long id) {
        return hotelRepository.findById(id)
                .map(HotelMapper::toDetailsDTO)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
    }


    public List<HotelSummaryDTO> search(HotelSearchRequest request) {
        Specification<Hotel> spec = buildSearchSpecification(request);

        return hotelRepository.findAll(spec).stream()
                .map(HotelMapper::toSummaryDTO)
                .toList();

    }

    private Specification<Hotel> buildSearchSpecification(HotelSearchRequest request) {
        List<Specification<Hotel>> specifications = new ArrayList<>();

        if (request.name() != null && !request.name().isBlank()) {
            specifications.add(HotelSpecifications.withName(request.name()));
        }
        if (request.brand() != null && !request.brand().isBlank()) {
            specifications.add(HotelSpecifications.withBrand(request.brand()));
        }
        if (request.city() != null && !request.city().isBlank()) {
            specifications.add(HotelSpecifications.withCity(request.city()));
        }
        if (request.country() != null && !request.country().isBlank()) {
            specifications.add(HotelSpecifications.withCountry(request.country()));
        }
        if (request.amenities() != null && !request.amenities().isEmpty()) {
            specifications.add(HotelSpecifications.withAmenities(request.amenities()));
        }

        return specifications.isEmpty()
                ? null
                : Specification.allOf(specifications);
    }

    public HotelSummaryDTO createHotel(HotelCreationDto request) {
        Hotel hotel = HotelMapper.fromCreationDtoToHotel(request);
        Hotel savedHotel = hotelRepository.save(hotel);
        return HotelMapper.toSummaryDTO(savedHotel);
    }
}
