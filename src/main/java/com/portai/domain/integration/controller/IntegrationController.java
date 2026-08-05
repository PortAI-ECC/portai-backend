package com.portai.domain.integration.controller;

import com.portai.domain.integration.dto.IntegrationRequest;
import com.portai.domain.integration.dto.IntegrationResponse;
import com.portai.domain.integration.service.IntegrationService;
import com.portai.global.annotation.AuthUser;
import com.portai.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationService integrationService;

    // 연동된 플랫폼 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<IntegrationResponse>>> getMyIntegrations(@AuthUser Long userId) {
        List<IntegrationResponse> responses = integrationService.getMyIntegrations(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 플랫폼 연동 등록 (URL)
    @PostMapping
    public ResponseEntity<ApiResponse<IntegrationResponse>> registerIntegration(
            @AuthUser Long userId,
            @Valid @RequestBody IntegrationRequest request) {
        IntegrationResponse response = integrationService.registerIntegration(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 연동 해제
    @DeleteMapping("/{integrationId}")
    public ResponseEntity<ApiResponse<Void>> deleteIntegration(
            @AuthUser Long userId,
            @PathVariable Long integrationId) {
        integrationService.deleteIntegration(userId, integrationId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 수동 재수집 트리거
    @PostMapping("/{integrationId}/sync")
    public ResponseEntity<ApiResponse<IntegrationResponse>> triggerResync(
            @AuthUser Long userId,
            @PathVariable Long integrationId) {
        IntegrationResponse response = integrationService.triggerResync(userId, integrationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 수집 상태 조회
    @GetMapping("/{integrationId}/sync-status")
    public ResponseEntity<ApiResponse<IntegrationResponse>> getSyncStatus(
            @AuthUser Long userId,
            @PathVariable Long integrationId) {
        IntegrationResponse response = integrationService.getSyncStatus(userId, integrationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
