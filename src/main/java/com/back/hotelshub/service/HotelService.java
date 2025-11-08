package com.back.hotelshub.service;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.Amenity;
import com.back.hotelshub.entity.Hotel;
import com.back.hotelshub.mapper.HotelMapper;
import com.back.hotelshub.repository.AmenityRepository;
import com.back.hotelshub.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

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

    public void addAmenitiesToHotelById(Long id, List<String> amenityNames) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));

        Set<Amenity> amenities = amenityNames.stream()
                .map(name -> amenityRepository.findByAmenityNameIgnoreCase(name)
                        .orElseGet(() -> amenityRepository.save(
                                Amenity.builder().amenityName(name).build()
                        ))
                )
                .collect(Collectors.toSet());


        hotel.getAmenities().addAll(amenities);
        Hotel saved = hotelRepository.save(hotel);
    }

    public Map<String, Long> getHistogramByParam(String param) {
        List<Object[]> result = hotelRepository.getHistogramByParam(param);
        return result.stream()
                .collect(Collectors.toMap(
                        r -> String.valueOf(r[0]),
                        r -> ((Number) r[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
