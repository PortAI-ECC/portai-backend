package com.portai.domain.jobposting.controller;

import com.portai.domain.jobposting.dto.JobPostingRequest;
import com.portai.domain.jobposting.dto.JobPostingResponse;
import com.portai.domain.jobposting.service.JobPostingService;
import com.portai.global.annotation.AuthUser;
import com.portai.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // 채용공고 등록 (URL/PDF)
    @PostMapping
    public ResponseEntity<ApiResponse<JobPostingResponse>> registerJobPosting(
            @AuthUser Long userId,
            @Valid @RequestBody JobPostingRequest request) {
        JobPostingResponse response = jobPostingService.registerJobPosting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 채용공고 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPostingResponse>>> getMyJobPostings(@AuthUser Long userId) {
        List<JobPostingResponse> responses = jobPostingService.getMyJobPostings(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 채용공고 상세 조회 (분석 상태/결과 포함)
    @GetMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<JobPostingResponse>> getJobPosting(
            @AuthUser Long userId,
            @PathVariable Long jobPostingId) {
        JobPostingResponse response = jobPostingService.getJobPosting(userId, jobPostingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 채용공고 삭제
    @DeleteMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(
            @AuthUser Long userId,
            @PathVariable Long jobPostingId) {
        jobPostingService.deleteJobPosting(userId, jobPostingId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 분석 (재)트리거
    @PostMapping("/{jobPostingId}/analyze")
    public ResponseEntity<ApiResponse<JobPostingResponse>> triggerAnalysis(
            @AuthUser Long userId,
            @PathVariable Long jobPostingId) {
        JobPostingResponse response = jobPostingService.triggerAnalysis(userId, jobPostingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
