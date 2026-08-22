package com.portai.domain.career.dto;

import com.portai.domain.career.entity.Career;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CareerResponse {
    private Long careerId;
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;

    private String duties;
    private String achievements;
    private String freeText;

    public static CareerResponse from(Career career) {
        return CareerResponse.builder()
                .careerId(career.getId())
                .companyName(career.getCompanyName())
                .position(career.getPosition())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .duties(career.getDuties())
                .achievements(career.getAchievements())
                .freeText(career.getFreeText())
                .build();
    }
}