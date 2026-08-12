package com.portai.domain.contest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ContestCreateRequest {
    @NotBlank(message = "공모전명은 필수 입력값입니다.")
    private String name;
    private String host;
    private LocalDate startDate; // JSON에서는 "startDate": "2026-03-01" 형태로 들어옵니다.
    private LocalDate endDate;
    private Boolean awarded;     // JSON에서는 "awarded": true/false 형태로 들어옵니다.
    private String role;
    private String result;
}