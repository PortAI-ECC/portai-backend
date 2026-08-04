package com.portai.domain.integration.service;

import com.portai.domain.integration.dto.IntegrationRequest;
import com.portai.domain.integration.dto.IntegrationResponse;
import com.portai.domain.integration.entity.Integration;
import com.portai.domain.integration.repository.IntegrationRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final IntegrationRepository integrationRepository;
    private final UserRepository userRepository;

    /**
     * 연동된 플랫폼 목록 조회
     */
    public List<IntegrationResponse> getMyIntegrations(Long userId) {
        return integrationRepository.findAllByUserId(userId).stream()
                .map(IntegrationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 플랫폼 연동 등록 (URL/계정)
     * - 같은 유저가 같은 플랫폼을 중복 등록할 수 없음
     * - 실제 데이터 수집(GitHub API 호출 등)은 infra/github 클라이언트 연동 후 비동기로 처리 예정
     */
    @Transactional
    public IntegrationResponse registerIntegration(Long userId, IntegrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (integrationRepository.existsByUserIdAndPlatform(userId, request.getPlatform())) {
            throw new CustomException(ErrorCode.DUPLICATE_INTEGRATION);
        }

        Integration integration = Integration.builder()
                .user(user)
                .platform(request.getPlatform())
                .value(request.getValue())
                .build();

        Integration saved = integrationRepository.save(integration);
        return new IntegrationResponse(saved);
    }

    /**
     * 연동 해제 (본인 소유만 가능)
     */
    @Transactional
    public void deleteIntegration(Long userId, Long integrationId) {
        Integration integration = findOwnedIntegrationOrThrow(userId, integrationId);
        integrationRepository.delete(integration);
    }

    /**
     * 수동 재수집 트리거 (본인 소유만 가능)
     * TODO: infra/github 클라이언트가 준비되면 비동기 수집 작업을 실제로 호출하도록 연결
     */
    @Transactional
    public IntegrationResponse triggerResync(Long userId, Long integrationId) {
        Integration integration = findOwnedIntegrationOrThrow(userId, integrationId);
        integration.startSync();
        return new IntegrationResponse(integration);
    }

    /**
     * 수집 상태 조회 (본인 소유만 가능)
     */
    public IntegrationResponse getSyncStatus(Long userId, Long integrationId) {
        Integration integration = findOwnedIntegrationOrThrow(userId, integrationId);
        return new IntegrationResponse(integration);
    }

    private Integration findOwnedIntegrationOrThrow(Long userId, Long integrationId) {
        return integrationRepository.findByIdAndUserId(integrationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTEGRATION_NOT_FOUND));
    }
}
