package com.back.hotelshub.unitTest;

import com.back.hotelshub.dto.*;
import com.back.hotelshub.entity.*;
import com.back.hotelshub.repository.AmenityRepository;
import com.back.hotelshub.repository.HotelRepository;
import com.back.hotelshub.service.HotelService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel sampleHotel;

    @BeforeEach
    void setup() {
        sampleHotel = createSampleHotel();
    }

    @Test
    @DisplayName("getAllHotels should return mapped summaries")
    void getAllHotels_returnsSummaries() {
        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));

        List<HotelSummaryDTO> result = hotelService.getAllHotels();

        assertThat(result).hasSize(1);
        HotelSummaryDTO dto = result.getFirst();
        assertThat(dto.name()).isEqualTo(sampleHotel.getName());
        assertThat(dto.address()).contains(sampleHotel.getAddress().getCity());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getHotelDetailsDTOById returns DTO when hotel exists")
    void getHotelDetailsById_found() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(sampleHotel));

        HotelDetailsDTO dto = hotelService.getHotelDetailsDTOById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(sampleHotel.getId());
        assertThat(dto.name()).isEqualTo(sampleHotel.getName());
        assertThat(dto.address().city()).isEqualTo(sampleHotel.getAddress().getCity());
        verify(hotelRepository).findById(1L);
    }

    @Test
    @DisplayName("getHotelDetailsDTOById throws when hotel not found")
    void getHotelDetailsById_notFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelDetailsDTOById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Hotel not found");
        verify(hotelRepository).findById(999L);
    }

    @Test
    @DisplayName("search should delegate to repository and map results")
    void search_delegatesToRepository() {
        HotelSearchRequest req = new HotelSearchRequest("name", "brand", "city", "country", List.of("WiFi"));
        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleHotel));

        List<HotelSummaryDTO> out = hotelService.search(req);

        assertThat(out).hasSize(1);
        assertThat(out.getFirst().name()).isEqualTo(sampleHotel.getName());
        verify(hotelRepository).findAll(any(Specification.class));
    }

    // ---------- createHotel ----------
    @Test
    @DisplayName("createHotel should save and return summary DTO")
    void createHotel_savesAndReturns() {
        HotelCreationDto creation = new HotelCreationDto(
                "New Hotel",
                "desc",
                "BrandX",
                new AddressDTO(5, "Street", "CityA", "CountryA", "12345"),
                new ContactsDTO("+123456", "a@b.com"),
                new ArrivalTimeDTO("14:00", "12:00")
        );

        Hotel converted = com.back.hotelshub.mapper.HotelMapper.fromCreationDtoToHotel(creation);
        converted.setId(42L);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(converted);

        HotelSummaryDTO result = hotelService.createHotel(creation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.name()).isEqualTo("New Hotel");
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    @DisplayName("addAmenitiesToHotelById should add existing and create missing amenities")
    void addAmenities_existingAndNew() {
        Amenity existing = Amenity.builder().id(10L).amenityName("Free WiFi").build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(sampleHotel));
        when(amenityRepository.findByAmenityNameIgnoreCase("Free WiFi")).thenReturn(Optional.of(existing));

        Amenity newAmenity = Amenity.builder().id(20L).amenityName("New Amenity").build();
        when(amenityRepository.findByAmenityNameIgnoreCase("New Amenity")).thenReturn(Optional.empty());
        when(amenityRepository.save(argThat(a -> "New Amenity".equalsIgnoreCase(a.getAmenityName()))))
                .thenReturn(newAmenity);

        ArgumentCaptor<Hotel> savedCaptor = ArgumentCaptor.forClass(Hotel.class);
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        hotelService.addAmenitiesToHotelById(1L, List.of("Free WiFi", "New Amenity"));

        verify(amenityRepository).findByAmenityNameIgnoreCase("Free WiFi");
        verify(amenityRepository).findByAmenityNameIgnoreCase("New Amenity");
        verify(amenityRepository).save(argThat(a -> "New Amenity".equalsIgnoreCase(a.getAmenityName())));
        verify(hotelRepository).save(savedCaptor.capture());

        Hotel captured = savedCaptor.getValue();
        Set<String> names = captured.getAmenities().stream().map(Amenity::getAmenityName).collect(Collectors.toSet());
        assertThat(names).contains("Free WiFi", "New Amenity");
    }

    @Test
    @DisplayName("addAmenitiesToHotelById throws when hotel not found")
    void addAmenities_hotelNotFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.addAmenitiesToHotelById(999L, List.of("a")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Hotel not found");
    }

    @Test
    @DisplayName("getHistogramByParam should convert repository result to map")
    void getHistogramByParam_convertsCorrectly() {
        Object[] row1 = new Object[] { "Minsk", 3L };
        Object[] row2 = new Object[] { "Moscow", 2L };
        when(hotelRepository.getHistogramByParam("city")).thenReturn(List.of(row1, row2));

        Map<String, Long> map = hotelService.getHistogramByParam("city");

        assertThat(map).containsEntry("Minsk", 3L).containsEntry("Moscow", 2L);
        verify(hotelRepository).getHistogramByParam("city");
    }

    private static Hotel createSampleHotel() {
        Address addr = Address.builder()
                .id(5L)
                .houseNumber(1)
                .street("Kirova Street")
                .city("Mogilev")
                .country("Belarus")
                .postCode("220001")
                .build();

        Contact contact = new Contact();
        contact.setId(7L);
        contact.setPhone("+375 222 75-55-55");
        contact.setEmail("info@test.com");

        Set<Amenity> amenities = new HashSet<>();
        amenities.add(Amenity.builder().id(11L).amenityName("Free WiFi").build());

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckOut("14:00");
        arrivalTime.setCheckIn("12:00");

        Hotel hotel = Hotel.builder()
                .id(2L)
                .name("Radisson Blu Hotel")
                .description("Contemporary hotel featuring stylish rooms...")
                .brand("Radisson")
                .address(addr)
                .contact(contact)
                .arrivalTime(arrivalTime)
                .amenities(amenities)
                .build();

        return hotel;
    }
}
