package com.portai.domain.jobposting.dto;

import com.portai.domain.jobposting.entity.JobPosting;
import com.portai.domain.jobposting.entity.JobPostingStatus;
import com.portai.domain.jobposting.entity.SourceType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 채용공고 조회/분석 상태 응답 DTO
 */
@Getter
public class JobPostingResponse {

    private final Long id;
    private final SourceType sourceType;
    private final String sourceValue;
    private final JobPostingStatus status;
    private final List<String> requiredSkills;
    private final List<String> preferredSkills;
    private final BigDecimal matchScore;
    private final String failReason;
    private final LocalDateTime createdAt;

    public JobPostingResponse(JobPosting jobPosting) {
        this.id = jobPosting.getId();
        this.sourceType = jobPosting.getSourceType();
        this.sourceValue = jobPosting.getSourceValue();
        this.status = jobPosting.getStatus();
        this.requiredSkills = jobPosting.getRequiredSkills();
        this.preferredSkills = jobPosting.getPreferredSkills();
        this.matchScore = jobPosting.getMatchScore();
        this.failReason = jobPosting.getFailReason();
        this.createdAt = jobPosting.getCreatedAt();
    }
}
