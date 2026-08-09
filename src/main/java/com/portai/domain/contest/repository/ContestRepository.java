package com.portai.domain.contest.repository;

import com.portai.domain.contest.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    // 1. 특정 유저의 공모전 목록 전체 조회 (최신 등록순으로 정렬)
    List<Contest> findByUserIdOrderByIdDesc(Long userId);

    // 2. 수정/삭제 시 '내 공모전이 맞는지' 권한을 확인하기 위한 메서드
    Optional<Contest> findByIdAndUserId(Long id, Long userId);
}