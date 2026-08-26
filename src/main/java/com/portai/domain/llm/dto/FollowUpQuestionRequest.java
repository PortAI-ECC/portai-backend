package com.portai.domain.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowUpQuestionRequest {

    @NotBlank(message = "분야는 필수입니다.")
    private String category;

    private String freeText;
}