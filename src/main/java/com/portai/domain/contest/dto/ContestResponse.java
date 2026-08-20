package com.portai.domain.contest.dto;

import com.portai.domain.contest.entity.Contest;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class ContestResponse {
    private Long contestId; // 프론트엔드 식별을 위해 id 대신 contestId 사용
    private String name;
    private String host;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean awarded;
    private String role;
    private String result;
    private String freeText;

    // Entity -> DTO 변환 편의 메서드
    public static ContestResponse from(Contest contest) {
        return ContestResponse.builder()
                .contestId(contest.getId())
                .name(contest.getName())
                .host(contest.getHost())
                .startDate(contest.getStartDate())
                .endDate(contest.getEndDate())
                .awarded(contest.getAwarded())
                .role(contest.getRole())
                .result(contest.getResult())
                .freeText(contest.getFreeText())
                .build();
    }
}