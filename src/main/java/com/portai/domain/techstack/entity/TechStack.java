package com.portai.domain.techstack.entity;

import com.portai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기술 스택 (Tech Stack) 엔티티
 * - 사용자가 보유한 기술 스택 정보를 데이터베이스(tech_stacks 테이블)에 매핑
 * - 동일한 사용자가 같은 이름의 기술을 중복 등록할 수 없도록 복합 유니크 제약조건을 설정
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tech_stacks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_tech_name",
                        columnNames = {"user_id", "name"}
                )
        }
)
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 기술 스택을 소유한 사용자 (N:1 연관관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 기술명 (예: Python, React 등)
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 기술 분류 (기본값: OTHER)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechCategory category = TechCategory.OTHER;

    /**
     * 기술 숙련도 (기본값: INTERMEDIATE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Proficiency proficiency = Proficiency.INTERMEDIATE;

    /**
     * 기술 데이터 출처 (기본값: MANUAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechSource source = TechSource.MANUAL;

    @Builder
    public TechStack(User user, String name, TechCategory category, Proficiency proficiency, TechSource source) {
        this.user = user;
        this.name = name;
        this.category = (category != null) ? category : TechCategory.OTHER;
        this.proficiency = (proficiency != null) ? proficiency : Proficiency.INTERMEDIATE;
        this.source = (source != null) ? source : TechSource.MANUAL;
    }
}