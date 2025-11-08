package com.back.hotelshub.repository;

import com.back.hotelshub.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>,
        JpaSpecificationExecutor<Hotel>, HotelHistogramRepository  {

    @EntityGraph(attributePaths = {"address", "contact", "amenities"})
    List<Hotel> findAll(Specification<Hotel> spec);
}
