package com.portai.domain.generation.entity;

import com.portai.domain.generation.dto.RecordIds;
import com.portai.domain.jobposting.entity.JobPosting;
import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class Generation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 생성 요청자 (N:1) - user 도메인의 User 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 채용공고 맞춤형이 아니면 NULL (지호/job-postings 도메인 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting;

    @Column(length = 30)
    private String style;

    // REQ-026: 사용자가 선택한 포트폴리오 템플릿 (프론트 TEMPLATES 목록의 id, 예: "template-1")
    @Column(name = "template_id", length = 50)
    private String templateId;
    
    // 결과물 생성에 포함할 기록을 분야별로 지정 (contests/careers/certificates/education/techStacks/activities)
    @Convert(converter = RecordIdsJsonConverter.class)
    @Column(name = "record_ids", columnDefinition = "JSON")
    private RecordIds recordIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_status", nullable = false, length = 20)
    private GenerationOverallStatus overallStatus;

    // 생성 요청 하나에 여러 결과물(이력서/포트폴리오 등)이 딸려있는 구조 - Generation이 애그리거트 루트
    @OneToMany(mappedBy = "generation", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<GenerationResult> results = new ArrayList<>();

    @Builder
    public Generation(User user, JobPosting jobPosting, String style, String templateId, RecordIds recordIds) {
        this.user = user;
        this.jobPosting = jobPosting;
        this.style = style;
        this.templateId = templateId;
        this.recordIds = recordIds;
        this.overallStatus = GenerationOverallStatus.IN_PROGRESS;
    }

    // 결과물 항목 추가 (양방향 연관관계 편의 메서드)
    public void addResult(GenerationResult result) {
        this.results.add(result);
        result.assignGeneration(this);
    }

    // 하위 결과물들의 상태를 보고 전체 상태를 재계산
    public void refreshOverallStatus() {
        boolean anyInProgress = results.stream().anyMatch(r -> r.getStatus() == GenerationResultStatus.IN_PROGRESS);
        if (anyInProgress) {
            this.overallStatus = GenerationOverallStatus.IN_PROGRESS;
            return;
        }

        boolean anyFailed = results.stream().anyMatch(r -> r.getStatus() == GenerationResultStatus.FAILED);
        boolean anyCompleted = results.stream().anyMatch(r -> r.getStatus() == GenerationResultStatus.COMPLETED);

        if (anyFailed && anyCompleted) {
            this.overallStatus = GenerationOverallStatus.PARTIALLY_COMPLETED;
        } else if (anyFailed) {
            this.overallStatus = GenerationOverallStatus.FAILED;
        } else {
            this.overallStatus = GenerationOverallStatus.COMPLETED;
        }
    }
}
