package com.daebangg.sayroam.travelstyle.domain;

import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

// 여행 스타일 질문. 질문을 없앨 때 행을 지우면 기존 답변이 깨지므로 active를 false로 바꾼다
@Entity
@Table(name = "travel_style_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelStyleItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String minLabel;

    @Column(nullable = false, length = 30)
    private String maxLabel;

    @Column(nullable = false)
    private int sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Builder
    private TravelStyleItem(String name, String minLabel, String maxLabel, int sortOrder) {
        this.name = name;
        this.minLabel = minLabel;
        this.maxLabel = maxLabel;
        this.sortOrder = sortOrder;
        this.active = true;
    }

}
