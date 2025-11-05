package com.back.hotelshub.service;

import com.back.hotelshub.dto.HotelDetailsDTO;
import com.back.hotelshub.dto.HotelSummaryDTO;
import com.back.hotelshub.mapper.HotelMapper;
import com.back.hotelshub.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelSummaryDTO> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(HotelMapper::ToDTO)
                .toList();
    }

    public HotelDetailsDTO getHotelDetailsDTOById(Long id) {
        return hotelRepository.findById(id)
                .map(HotelMapper::toDetailsDTO)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
    }
}
