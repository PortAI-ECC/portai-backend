package com.portai.domain.contest.entity;

import com.portai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "contests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자는 안전하게 PROTECTED로 설정
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 엔티티와 다대일(N:1) 관계 매핑 (유저 1명이 여러 공모전 보유)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String host;

    // DB의 DATE 타입은 자바의 LocalDate와 매핑됩니다.
    private LocalDate startDate;

    private LocalDate endDate;

    // DB 기본값이 FALSE이므로, null이 들어오지 않도록 처리
    @Column(nullable = false)
    private Boolean awarded;

    @Column(length = 100)
    private String role;

    @Column(length = 200)
    private String result;

    @Builder
    public Contest(User user, String name, String host, LocalDate startDate, LocalDate endDate, Boolean awarded, String role, String result) {
        this.user = user;
        this.name = name;
        this.host = host;
        this.startDate = startDate;
        this.endDate = endDate;
        // 생성 시 awarded 값이 안 들어오면 기본값 false 설정
        this.awarded = awarded != null ? awarded : false;
        this.role = role;
        this.result = result;
    }

    // 💡 수정(PATCH) API를 위한 비즈니스 메서드 (Dirty Checking 활용)
    public void updateContest(String name, String host, LocalDate startDate, LocalDate endDate, Boolean awarded, String role, String result) {
        this.name = name;
        this.host = host;
        this.startDate = startDate;
        this.endDate = endDate;
        this.awarded = awarded != null ? awarded : false;
        this.role = role;
        this.result = result;
    }
}