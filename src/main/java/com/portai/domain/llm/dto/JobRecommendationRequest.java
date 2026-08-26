package com.portai.domain.llm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobRecommendationRequest {

    @NotNull(message = "생성 결과 ID는 필수입니다.")
    private Long generationId;
}