package com.back.hotelshub.repository;

import com.back.hotelshub.entity.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HotelHistogramRepositoryImpl implements HotelHistogramRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Object[]> getHistogramByParam(String param) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Hotel> root = cq.from(Hotel.class);

        Path<?> groupByPath;

        switch (param.toLowerCase()) {
            case "brand" -> groupByPath = root.get("brand");
            case "city" -> groupByPath = root.join("address").get("city");
            case "country" -> groupByPath = root.join("address").get("country");
            case "amenities" -> groupByPath = root.joinSet("amenities").get("amenityName");
            default -> throw new IllegalArgumentException("Unsupported parameter: " + param);
        }

        cq.multiselect(groupByPath, cb.count(root.get("id")))
                .where(cb.isNotNull(groupByPath))
                .groupBy(groupByPath)
                .orderBy(cb.desc(cb.count(root.get("id"))));

        return entityManager.createQuery(cq).getResultList();
    }
}
