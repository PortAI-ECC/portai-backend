package com.portai.domain.education.dto;

import com.portai.domain.education.entity.Degree;
import com.portai.domain.education.entity.EducationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EducationRequest {

    @NotBlank(message = "학교명은 필수입니다.")
    private String school;

    private Degree degree;

    private String major;

    private String doubleMajor;

    private BigDecimal gpaScore;

    private BigDecimal gpaScale;

    private EducationStatus status;

    private LocalDate expectedGraduation;
}