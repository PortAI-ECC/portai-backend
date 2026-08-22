package com.portai.domain.integration.repository;

import com.portai.domain.integration.entity.Integration;
import com.portai.domain.integration.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationRepository extends JpaRepository<Integration, Long> {

    // 특정 유저가 등록한 연동 목록 조회
    List<Integration> findAllByUserId(Long userId);

    // 중복 등록 검증용 (한 유저가 같은 플랫폼을 두 번 등록할 수 없음)
    boolean existsByUserIdAndPlatform(Long userId, Platform platform);

    Optional<Integration> findByIdAndUserId(Long id, Long userId);
}
