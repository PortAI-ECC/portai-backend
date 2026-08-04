package com.portai.domain.jobposting.dto;

import com.portai.domain.jobposting.entity.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채용공고 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
public class JobPostingRequest {

    @NotNull(message = "입력 방식(URL/PDF)은 필수입니다.")
    private SourceType sourceType;

    @NotBlank(message = "채용공고 URL 또는 파일 경로는 필수입니다.")
    private String sourceValue;
}
