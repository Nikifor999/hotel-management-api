package com.back.hotelshub.dto;

import com.back.hotelshub.entity.Amenity;
import com.back.hotelshub.entity.Address;
import com.back.hotelshub.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

public final class HotelSpecifications {
    private HotelSpecifications() {}

    public static Specification<Hotel> withName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Hotel> withBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Hotel> withCity(String city) {
        return (root, query, cb) -> {
            if (city == null) return null;
            Join<Hotel, Address> address = root.join("address");
            return cb.equal(cb.lower(address.get("city")), city.toLowerCase());
        };
    }

    public static Specification<Hotel> withCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isBlank()) return null;
            Join<Hotel, Address> addr = root.join("address", JoinType.LEFT);
            return cb.equal(cb.lower(addr.get("country")), country.trim().toLowerCase());
        };
    }

    public static Specification<Hotel> withAmenities(List<String> amenityNames) {
        return (root, query, cb) -> {
            if (amenityNames == null || amenityNames.isEmpty()) return null;

            // Subquery returns hotel ids that contain all requested amenities
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Hotel> subRoot = sub.from(Hotel.class);
            Join<Hotel, Amenity> subJoin = subRoot.joinSet("amenities", JoinType.INNER);

            List<String> lower = amenityNames.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            sub.select(subRoot.get("id"))
                    .where(cb.equal(subRoot.get("id"), root.get("id")),
                            cb.lower(subJoin.get("amenityName")).in(lower))
                    .groupBy(subRoot.get("id"))
                    .having(cb.equal(cb.countDistinct(subJoin.get("id")), lower.size()));

            return cb.exists(sub);
        };
    }
}
