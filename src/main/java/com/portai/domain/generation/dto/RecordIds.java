package com.portai.domain.generation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 결과물 생성 시 "포함할 기록"을 분야별로 지정하기 위한 DTO.
 * 지정하지 않은 분야는 null로 유지되며, null인 경우 서비스 로직에서
 * 계정의 해당 분야 기록 전체를 사용하도록 처리할 수 있음(또는 미포함 처리 - 정책은 추후 확정).
 */
@Getter
@NoArgsConstructor
public class RecordIds {

    private List<Long> contests;
    private List<Long> careers;
    private List<Long> certificates;
    private List<Long> education;
    private List<Long> techStacks;
    private List<Long> activities;
}