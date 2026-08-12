package com.portai.domain.auth.repository;

import com.portai.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰 갱신
    Optional<RefreshToken> findByUserEmail(String userEmail);

    // 로그아웃
    void deleteByUserEmail(String userEmail);
}