package com.portai.domain.techstack.repository;

import com.portai.domain.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * 기술 스택(TechStack) 데이터베이스 접근을 위한 레포지토리 인터페이스
 */
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    // 1. 특정 유저의 모든 기술 스택 목록 조회
    List<TechStack> findByUserId(Long userId);

    // 2. 기술 스택 개별 추가 시, 이미 등록된 기술인지 중복 검사 (유저 ID + 기술 이름)
    boolean existsByUserIdAndName(Long userId, String name);

    // 3. 기술 수정/삭제 시, 본인의 기술 스택이 맞는지 검증하고 조회하기 위함
    Optional<TechStack> findByIdAndUserId(Long id, Long userId);
}