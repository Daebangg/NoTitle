package com.daebangg.sayroam.notification.domain;

import com.daebangg.sayroam.global.entity.BaseTimeEntity;
import com.daebangg.sayroam.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = @Index(name = "idx_notification_receiver_id", columnList = "receiver_id, id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private NotificationTargetType targetType;

    // 대상이 여러 테이블에 걸쳐 있고 삭제돼도 알림은 남아야 해서 FK 없이 값으로만 둔다
    private Long targetId;

    // 만든 시점의 문구를 저장해 닉네임 변경·탈퇴와 무관하게 그대로 보여준다
    @Column(nullable = false)
    private String content;

    private LocalDateTime readAt;

    @Builder
    private Notification(User receiver, NotificationType type, NotificationTargetType targetType, Long targetId,
                         String content) {
        this.receiver = receiver;
        this.type = type;
        this.targetType = targetType;
        this.targetId = targetId;
        this.content = content;
    }

}
