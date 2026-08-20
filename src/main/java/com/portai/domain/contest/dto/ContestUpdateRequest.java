package com.portai.domain.contest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ContestUpdateRequest {
    private String name;
    private String host;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean awarded;
    private String role;
    private String result;
    private String freeText;
}