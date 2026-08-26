package com.portai.domain.llm.controller;

import com.portai.domain.llm.dto.FollowUpQuestionRequest;
import com.portai.domain.llm.dto.JobRecommendationRequest;
import com.portai.domain.llm.service.AiService;
import com.portai.global.annotation.AuthUser;
import com.portai.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 선택한 분야와 자유 텍스트를 바탕으로 확장 질문을 생성한다.
     */
    @PostMapping("/follow-up-questions")
    public ResponseEntity<ApiResponse<List<String>>>
    generateFollowUpQuestions(
            @Valid @RequestBody FollowUpQuestionRequest request) {

        List<String> questions =
                aiService.generateFollowUpQuestions(request);

        return ResponseEntity.ok(
                ApiResponse.success(questions));
    }

    /**
     * 특정 generationId의 결과물을 바탕으로 적합한 직무를 추천한다.
     */
    @PostMapping("/job-recommendations")
    public ResponseEntity<ApiResponse<List<String>>>
    recommendJobs(
            @AuthUser Long userId,
            @Valid @RequestBody JobRecommendationRequest request) {

        List<String> jobs =
                aiService.recommendJobs(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(jobs));
    }
}