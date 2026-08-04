package com.portai.domain.jobposting.service;

import com.portai.domain.jobposting.dto.JobPostingResponse;
import com.portai.domain.jobposting.dto.JobPostingUrlRequest;
import com.portai.domain.jobposting.entity.JobPosting;
import com.portai.domain.jobposting.entity.SourceType;
import com.portai.domain.jobposting.repository.JobPostingRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.global.util.LocalFileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private static final String UPLOAD_SUB_DIR = "job-postings";

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final LocalFileStorage fileStorage;

    /**
     * URL로 채용공고 분석 요청.
     * 실제 분석(필수/우대 기술 추출, 매칭 점수 계산)은 LLM 클라이언트(infra/llmclient) 연동 후 비동기로 처리 예정
     */
    @Transactional
    public JobPostingResponse registerByUrl(Long userId, JobPostingUrlRequest request) {
        User user = getUserOrThrow(userId);

        JobPosting jobPosting = JobPosting.builder()
                .user(user)
                .sourceType(SourceType.URL)
                .sourceValue(request.getUrl())
                .build();

        return new JobPostingResponse(jobPostingRepository.save(jobPosting));
    }

    /**
     * PDF 파일로 채용공고 분석 요청.
     * 파일은 우선 로컬 스토리지(LocalFileStorage)에 저장 - 추후 S3 등으로 교체 예정
     */
    @Transactional
    public JobPostingResponse registerByPdf(Long userId, MultipartFile file) {
        User user = getUserOrThrow(userId);

        String storedPath = fileStorage.store(file, UPLOAD_SUB_DIR);

        JobPosting jobPosting = JobPosting.builder()
                .user(user)
                .sourceType(SourceType.PDF)
                .sourceValue(storedPath)
                .build();

        return new JobPostingResponse(jobPostingRepository.save(jobPosting));
    }

    /**
     * 내 채용공고(분석 이력) 목록 조회 (최신순)
     */
    public List<JobPostingResponse> getMyJobPostings(Long userId) {
        return jobPostingRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(JobPostingResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 분석 결과 상세 조회 (본인 소유만 가능)
     */
    public JobPostingResponse getJobPosting(Long userId, Long jobPostingId) {
        return new JobPostingResponse(findOwnedJobPostingOrThrow(userId, jobPostingId));
    }

    /**
     * 분석 이력 삭제 (본인 소유만 가능)
     */
    @Transactional
    public void deleteJobPosting(Long userId, Long jobPostingId) {
        jobPostingRepository.delete(findOwnedJobPostingOrThrow(userId, jobPostingId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    private JobPosting findOwnedJobPostingOrThrow(Long userId, Long jobPostingId) {
        return jobPostingRepository.findByIdAndUserId(jobPostingId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POSTING_NOT_FOUND));
    }
}
