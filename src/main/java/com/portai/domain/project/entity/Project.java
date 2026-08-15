package com.portai.domain.project.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 프로젝트 소유자 (N:1) - user 도메인의 User 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // GitHub 연동(지호/integrations)에서 채워질 저장소 URL
    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Builder
    public Project(User user, String title, String description,
                    LocalDate startDate, LocalDate endDate, String githubUrl) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.githubUrl = githubUrl;
    }

    // 수정 API용 업데이트 메서드 (엔티티는 불변 지향, setter 대신 의미 있는 메서드 사용)
    public void update(String title, String description, LocalDate startDate, LocalDate endDate, String githubUrl) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.githubUrl = githubUrl;
    }
}
