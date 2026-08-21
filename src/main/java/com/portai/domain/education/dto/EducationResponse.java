package com.portai.domain.education.dto;

import com.portai.domain.education.entity.Degree;
import com.portai.domain.education.entity.Education;
import com.portai.domain.education.entity.EducationStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class EducationResponse {

    private Long id;
    private String school;
    private Degree degree;
    private String major;
    private String doubleMajor;
    private BigDecimal gpaScore;
    private BigDecimal gpaScale;
    private EducationStatus status;
    private LocalDate expectedGraduation;
    private String freeText;

    public static EducationResponse from(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .school(education.getSchool())
                .degree(education.getDegree())
                .major(education.getMajor())
                .doubleMajor(education.getDoubleMajor())
                .gpaScore(education.getGpaScore())
                .gpaScale(education.getGpaScale())
                .status(education.getStatus())
                .expectedGraduation(education.getExpectedGraduation())
                .freeText(education.getFreeText())
                .build();
    }
}