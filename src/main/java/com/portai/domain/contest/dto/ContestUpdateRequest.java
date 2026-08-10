package com.portai.domain.contest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ContestUpdateRequest {
    @NotBlank(message = "공모전명은 필수 입력값입니다.")
    private String name;
    private String host;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean awarded;
    private String role;
    private String result;
}