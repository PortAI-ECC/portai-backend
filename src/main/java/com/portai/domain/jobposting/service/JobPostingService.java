package com.portai.domain.jobposting.service;

import com.portai.domain.jobposting.dto.JobPostingRequest;
import com.portai.domain.jobposting.dto.JobPostingResponse;
import com.portai.domain.jobposting.entity.JobPosting;
import com.portai.domain.jobposting.repository.JobPostingRepository;
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
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    /**
     * 채용공고 등록 (URL 또는 PDF)
     * 실제 분석(필수/우대 기술 추출, 매칭 점수 계산)은 LLM 클라이언트(infra/llmclient) 연동 후 비동기로 처리 예정
     */
    @Transactional
    public JobPostingResponse registerJobPosting(Long userId, JobPostingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        JobPosting jobPosting = JobPosting.builder()
                .user(user)
                .sourceType(request.getSourceType())
                .sourceValue(request.getSourceValue())
                .build();

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return new JobPostingResponse(saved);
    }

    /**
     * 내 채용공고 목록 조회 (최신순)
     */
    public List<JobPostingResponse> getMyJobPostings(Long userId) {
        return jobPostingRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(JobPostingResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 채용공고 상세 조회 (본인 소유만 가능)
     */
    public JobPostingResponse getJobPosting(Long userId, Long jobPostingId) {
        JobPosting jobPosting = findOwnedJobPostingOrThrow(userId, jobPostingId);
        return new JobPostingResponse(jobPosting);
    }

    /**
     * 채용공고 삭제 (본인 소유만 가능)
     */
    @Transactional
    public void deleteJobPosting(Long userId, Long jobPostingId) {
        JobPosting jobPosting = findOwnedJobPostingOrThrow(userId, jobPostingId);
        jobPostingRepository.delete(jobPosting);
    }

    /**
     * 분석 (재)트리거 (본인 소유만 가능)
     * TODO: infra/llmclient 연동 후 실제 분석 로직 호출로 교체
     */
    @Transactional
    public JobPostingResponse triggerAnalysis(Long userId, Long jobPostingId) {
        JobPosting jobPosting = findOwnedJobPostingOrThrow(userId, jobPostingId);
        jobPosting.startAnalysis();
        return new JobPostingResponse(jobPosting);
    }

    private JobPosting findOwnedJobPostingOrThrow(Long userId, Long jobPostingId) {
        return jobPostingRepository.findByIdAndUserId(jobPostingId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POSTING_NOT_FOUND));
    }
}
