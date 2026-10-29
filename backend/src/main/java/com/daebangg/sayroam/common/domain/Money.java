package com.daebangg.sayroam.common.domain;

import com.daebangg.sayroam.global.exception.BusinessException;
import com.daebangg.sayroam.global.exception.CommonErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// 원 단위 금액. N빵 나머지 때문에 0원 몫이 생길 수 있어 0은 허용한다
@Embeddable
public record Money(
        @Column(name = "amount", nullable = false)
        Long amount
) {

    public Money {
        if (amount == null || amount < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }

}
