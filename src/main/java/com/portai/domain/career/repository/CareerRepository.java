package com.portai.domain.career.repository;

import com.portai.domain.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRepository extends JpaRepository<Career, Long> {

    // 1. 특정 유저의 인턴/경력 목록 전체 조회 (최신 등록순)
    List<Career> findByUserIdOrderByIdDesc(Long userId);

    // 2. 수정/삭제 시 내 경력이 맞는지 권한 확인
    Optional<Career> findByIdAndUserId(Long id, Long userId);
}