package com.portai.domain.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ActivityRequest {

    @NotBlank(message = "활동명은 필수입니다.")
    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String role;

    private String description;

    private String freeText;
}