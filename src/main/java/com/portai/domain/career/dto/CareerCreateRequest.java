package com.portai.domain.career.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CareerCreateRequest {

    @NotBlank(message = "회사명은 필수 입력값입니다.")
    private String companyName;

    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String duties;
    private String achievements;
    private String freeText;
}