package com.portai.domain.jobposting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * URL로 채용공고 분석 요청 DTO
 */
@Getter
@NoArgsConstructor
public class JobPostingUrlRequest {

    @NotBlank(message = "채용공고 URL은 필수입니다.")
    private String url;
}
