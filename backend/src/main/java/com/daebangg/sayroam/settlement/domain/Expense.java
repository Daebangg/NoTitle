package com.daebangg.sayroam.settlement.domain;

import com.daebangg.sayroam.common.domain.Money;
import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import com.daebangg.sayroam.trip.domain.Trip;
import com.daebangg.sayroam.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    // 결제자가 아닌 사람이 대신 등록할 수 있어 따로 둔다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 100)
    private String title;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SplitType splitType;

    @Column(nullable = false)
    private LocalDateTime spentAt;

    // 분담은 지출과 생명주기가 같아 함께 저장·삭제한다
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    @Builder
    private Expense(Trip trip, User payer, User createdBy, String title, Money amount, SplitType splitType,
                    LocalDateTime spentAt) {
        this.trip = trip;
        this.payer = payer;
        this.createdBy = createdBy;
        this.title = title;
        this.amount = amount;
        this.splitType = splitType;
        this.spentAt = spentAt;
    }

    public void addShare(User user, Money amount) {
        shares.add(new ExpenseShare(this, user, amount));
    }

}
