package com.portai.domain.integration.dto;

import com.portai.domain.integration.entity.Integration;
import com.portai.domain.integration.entity.IntegrationStatus;
import com.portai.domain.integration.entity.Platform;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 연동 조회/상태 응답 DTO
 */
@Getter
public class IntegrationResponse {

    private final Long id;
    private final Platform platform;
    private final String value;
    private final IntegrationStatus status;
    private final LocalDateTime lastSyncedAt;
    private final LocalDateTime createdAt;

    public IntegrationResponse(Integration integration) {
        this.id = integration.getId();
        this.platform = integration.getPlatform();
        this.value = integration.getValue();
        this.status = integration.getStatus();
        this.lastSyncedAt = integration.getLastSyncedAt();
        this.createdAt = integration.getCreatedAt();
    }
}
