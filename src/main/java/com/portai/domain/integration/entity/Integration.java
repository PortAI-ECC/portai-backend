package com.portai.domain.integration.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "integrations",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_platform", columnNames = {"user_id", "platform"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class Integration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 연동 소유자 (N:1) - user 도메인의 User 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    // 등록한 URL/계정 (예: GitHub 프로필 URL)
    @Column(nullable = false, length = 255)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IntegrationStatus status;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Builder
    public Integration(User user, Platform platform, String value) {
        this.user = user;
        this.platform = platform;
        this.value = value;
        this.status = IntegrationStatus.PENDING;
    }

    // 재수집 트리거 시 상태를 IN_PROGRESS로 전환
    public void startSync() {
        this.status = IntegrationStatus.IN_PROGRESS;
    }

    // 수집 완료 시 상태 전환 + 완료 시각 기록
    public void completeSync() {
        this.status = IntegrationStatus.COMPLETED;
        this.lastSyncedAt = LocalDateTime.now();
    }

    // 수집 실패 시 상태 전환
    public void failSync() {
        this.status = IntegrationStatus.FAILED;
    }
}
