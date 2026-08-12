package com.portai.domain.generation.repository;

import com.portai.domain.generation.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenerationRepository extends JpaRepository<Generation, Long> {

    // 특정 유저의 생성 요청(생성 이력) 목록 조회 (최신순)
    List<Generation> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Generation> findByIdAndUserId(Long id, Long userId);
}
