package com.daebangg.sayroam.place.domain;

import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

// 외부 지도 API 장소 캐시. updatedAt이 외부 정보를 마지막으로 받아온 시각이다
@Entity
@Table(
        name = "place",
        uniqueConstraints = @UniqueConstraint(name = "uk_place_provider_external_id", columnNames = {"provider", "external_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceProvider provider;

    @Column(nullable = false, length = 100)
    private String externalId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String categoryCode;

    private String categoryName;

    private String roadAddress;

    private String address;

    @Embedded
    private Coordinate coordinate;

    @Builder
    private Place(PlaceProvider provider, String externalId, String name, String categoryCode, String categoryName,
                  String roadAddress, String address, Coordinate coordinate) {
        this.provider = provider;
        this.externalId = externalId;
        this.name = name;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.roadAddress = roadAddress;
        this.address = address;
        this.coordinate = coordinate;
    }

}
