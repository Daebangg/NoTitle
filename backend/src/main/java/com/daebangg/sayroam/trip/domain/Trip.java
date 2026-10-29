package com.daebangg.sayroam.trip.domain;

import com.daebangg.sayroam.common.domain.TravelPeriod;
import com.daebangg.sayroam.companion.domain.CompanionRequest;
import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import com.daebangg.sayroam.room.domain.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

// 여행 상태(계획 중/여행 중/종료)는 저장하지 않고 여행 기간과 오늘 날짜로 계산한다
@Entity
@Table(name = "trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, unique = true)
    private Room room;

    // 동행 매칭으로 만들어진 여행이면 그 요청, 친구끼리 만든 여행은 NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_companion_request_id")
    private CompanionRequest sourceCompanionRequest;

    @Column(length = 50)
    private String region;

    // 날짜 미정이면 NULL
    @Embedded
    private TravelPeriod period;

    @Builder
    private Trip(Room room, CompanionRequest sourceCompanionRequest, String region, TravelPeriod period) {
        this.room = room;
        this.sourceCompanionRequest = sourceCompanionRequest;
        this.region = region;
        this.period = period;
    }

}
