package com.portai.domain.jobposting.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "job_postings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 채용공고 등록자 (N:1) - user 도메인의 User 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 10)
    private SourceType sourceType;

    // URL이거나 업로드된 PDF 파일 저장 경로
    @Column(name = "source_value", length = 500)
    private String sourceValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobPostingStatus status;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "required_skills", columnDefinition = "JSON")
    private List<String> requiredSkills;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "preferred_skills", columnDefinition = "JSON")
    private List<String> preferredSkills;

    // 0.00 ~ 1.00 사이의 매칭 점수
    @Column(name = "match_score", precision = 4, scale = 2)
    private BigDecimal matchScore;

    @Column(name = "fail_reason", length = 100)
    private String failReason;

    @Builder
    public JobPosting(User user, SourceType sourceType, String sourceValue) {
        this.user = user;
        this.sourceType = sourceType;
        this.sourceValue = sourceValue;
        this.status = JobPostingStatus.PENDING;
    }

    // 분석 시작 (LLM/파싱 작업 트리거 시 호출)
    public void startAnalysis() {
        this.status = JobPostingStatus.IN_PROGRESS;
        this.failReason = null;
    }

    // 분석 완료 - 추출된 기술 키워드와 매칭 점수 반영
    public void completeAnalysis(List<String> requiredSkills, List<String> preferredSkills, BigDecimal matchScore) {
        this.status = JobPostingStatus.COMPLETED;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
        this.matchScore = matchScore;
    }

    // 분석 실패
    public void failAnalysis(String reason) {
        this.status = JobPostingStatus.FAILED;
        this.failReason = reason;
    }
}
