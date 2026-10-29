package com.daebangg.sayroam.trip.domain;

import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import com.daebangg.sayroam.place.domain.Place;
import com.daebangg.sayroam.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "trip_place",
        indexes = @Index(name = "idx_trip_place_trip_day_order", columnList = "trip_id, day_number, visit_order")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // 일차 미배정은 NULL
    private Integer dayNumber;

    private Integer visitOrder;

    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @Builder
    private TripPlace(Trip trip, Place place, Integer dayNumber, Integer visitOrder, String memo, User addedBy) {
        this.trip = trip;
        this.place = place;
        this.dayNumber = dayNumber;
        this.visitOrder = visitOrder;
        this.memo = memo;
        this.addedBy = addedBy;
    }

}
