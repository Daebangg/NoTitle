package com.daebangg.sayroam.place.domain;

import com.daebangg.sayroam.global.exception.BusinessException;
import com.daebangg.sayroam.global.exception.CommonErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record Coordinate(
        @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
        BigDecimal latitude,

        @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
        BigDecimal longitude
) {

    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

    public Coordinate {
        if (latitude == null || longitude == null
                || latitude.abs().compareTo(MAX_LATITUDE) > 0 || longitude.abs().compareTo(MAX_LONGITUDE) > 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }

}
