package com.portai.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 프로젝트 등록/수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private String githubUrl;
}
