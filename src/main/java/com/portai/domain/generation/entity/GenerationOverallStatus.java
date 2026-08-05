package com.portai.domain.generation.entity;

/**
 * 생성 요청 전체 상태 (하위 결과물들의 상태를 종합)
 */
public enum GenerationOverallStatus {
    IN_PROGRESS,
    COMPLETED,
    PARTIALLY_COMPLETED,
    FAILED
}
