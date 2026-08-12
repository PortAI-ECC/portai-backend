package com.portai.domain.jobposting.controller;

import com.portai.domain.jobposting.dto.JobPostingResponse;
import com.portai.domain.jobposting.dto.JobPostingUrlRequest;
import com.portai.domain.jobposting.service.JobPostingService;
import com.portai.global.annotation.AuthUser;
import com.portai.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // URL로 분석 요청
    @PostMapping("/url")
    public ResponseEntity<ApiResponse<JobPostingResponse>> registerByUrl(
            @AuthUser Long userId,
            @Valid @RequestBody JobPostingUrlRequest request) {
        JobPostingResponse response = jobPostingService.registerByUrl(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // PDF로 분석 요청
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<JobPostingResponse>> registerByPdf(
            @AuthUser Long userId,
            @RequestPart("file") MultipartFile file) {
        JobPostingResponse response = jobPostingService.registerByPdf(userId, file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 분석 이력 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPostingResponse>>> getMyJobPostings(@AuthUser Long userId) {
        List<JobPostingResponse> responses = jobPostingService.getMyJobPostings(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 분석 결과 상세 조회
    @GetMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<JobPostingResponse>> getJobPosting(
            @AuthUser Long userId,
            @PathVariable Long jobPostingId) {
        JobPostingResponse response = jobPostingService.getJobPosting(userId, jobPostingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 분석 이력 삭제
    @DeleteMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(
            @AuthUser Long userId,
            @PathVariable Long jobPostingId) {
        jobPostingService.deleteJobPosting(userId, jobPostingId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
