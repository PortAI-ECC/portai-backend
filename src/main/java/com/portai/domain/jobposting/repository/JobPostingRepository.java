package com.portai.domain.jobposting.repository;

import com.portai.domain.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    // 특정 유저가 등록한 채용공고 목록 조회 (최신순)
    List<JobPosting> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<JobPosting> findByIdAndUserId(Long id, Long userId);
}
