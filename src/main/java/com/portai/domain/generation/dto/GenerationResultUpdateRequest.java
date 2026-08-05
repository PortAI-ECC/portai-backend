package com.portai.domain.generation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 생성된 결과물을 직접 수정할 때 사용하는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class GenerationResultUpdateRequest {

    @NotBlank(message = "수정할 내용은 필수입니다.")
    private String content;
}
