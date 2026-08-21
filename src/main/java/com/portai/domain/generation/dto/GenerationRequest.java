package com.portai.domain.generation.dto;

import com.portai.domain.generation.entity.GenerationResultType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 결과물 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class GenerationRequest {

    // 채용공고 맞춤형 생성이 아니면 null (일반 생성)
    private Long jobPostingId;

    // 적용할 문체 (윤지/preferences 도메인의 style 값과 동일한 규칙 사용)
    private String style;

    // REQ-026: 선택한 포트폴리오 템플릿 id (프론트 TEMPLATES 목록 기준, 예: "template-1")
    private String templateId;

    @NotEmpty(message = "생성할 결과물 유형을 최소 1개 이상 선택해야 합니다.")
    private List<GenerationResultType> types;

    // 생성 결과물에 포함할 기록을 분야별로 지정 (미지정 시 null - 서비스에서 기본 정책 처리)
    private RecordIds recordIds;
}
