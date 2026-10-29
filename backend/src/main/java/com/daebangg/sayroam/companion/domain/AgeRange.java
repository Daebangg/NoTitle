package com.daebangg.sayroam.companion.domain;

import com.daebangg.sayroam.global.exception.BusinessException;
import com.daebangg.sayroam.global.exception.CommonErrorCode;
import jakarta.persistence.Embeddable;

// 한쪽이 NULL이면 그쪽은 제한 없음
@Embeddable
public record AgeRange(
        Integer minAge,
        Integer maxAge
) {

    public AgeRange {
        if ((minAge != null && minAge < 0) || (maxAge != null && maxAge < 0)
                || (minAge != null && maxAge != null && minAge > maxAge)) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }

}
