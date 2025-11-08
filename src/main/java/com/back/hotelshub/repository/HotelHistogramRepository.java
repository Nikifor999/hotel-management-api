package com.back.hotelshub.repository;

import java.util.List;

public interface HotelHistogramRepository {
    List<Object[]> getHistogramByParam(String param);
}
