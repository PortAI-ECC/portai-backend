package com.portai.domain.career.entity;

import com.portai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "careers") // DB 테이블명 매핑
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와 N:1 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(length = 100)
    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    // TEXT 타입 매핑
    @Column(columnDefinition = "TEXT")
    private String duties;

    @Column(columnDefinition = "TEXT")
    private String achievements;

    @Builder
    public Career(User user, String companyName, String position, LocalDate startDate, LocalDate endDate, String duties, String achievements) {
        this.user = user;
        this.companyName = companyName;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duties = duties;
        this.achievements = achievements;
    }

    // 프론트엔드에서 특정 값만 보냈을 때, 나머지 값이 null로 덮어씌워져 날아가는 것을 방지
    public void updateCareer(String companyName, String position, LocalDate startDate, LocalDate endDate, String duties, String achievements) {
        if (companyName != null) this.companyName = companyName;
        if (position != null) this.position = position;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
        if (duties != null) this.duties = duties;
        if (achievements != null) this.achievements = achievements;
    }
}