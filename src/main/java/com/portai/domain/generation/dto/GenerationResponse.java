package com.portai.domain.generation.dto;

import com.portai.domain.generation.entity.Generation;
import com.portai.domain.generation.entity.GenerationOverallStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 생성 요청 + 하위 결과물 목록 응답 DTO
 */
@Getter
public class GenerationResponse {

    private final Long id;
    private final Long jobPostingId;
    private final String style;
    private final String templateId;
    private final RecordIds recordIds;
    private final GenerationOverallStatus overallStatus;
    private final LocalDateTime createdAt;
    private final List<GenerationResultResponse> results;

    public GenerationResponse(Generation generation) {
        this.id = generation.getId();
        this.jobPostingId = generation.getJobPosting() != null ? generation.getJobPosting().getId() : null;
        this.style = generation.getStyle();
        this.templateId = generation.getTemplateId();
        this.recordIds = generation.getRecordIds();
        this.overallStatus = generation.getOverallStatus();
        this.createdAt = generation.getCreatedAt();
        this.results = generation.getResults().stream()
                .map(GenerationResultResponse::new)
                .collect(Collectors.toList());
    }
}
