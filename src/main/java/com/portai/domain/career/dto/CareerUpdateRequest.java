package com.portai.domain.career.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CareerUpdateRequest {
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String duties;
    private String achievements;
}