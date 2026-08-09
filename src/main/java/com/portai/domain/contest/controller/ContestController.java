package com.portai.domain.contest.controller;

import com.portai.domain.contest.dto.ContestCreateRequest;
import com.portai.domain.contest.dto.ContestResponse;
import com.portai.domain.contest.dto.ContestUpdateRequest;
import com.portai.domain.contest.service.ContestService;
import com.portai.global.annotation.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    /**
     * 1. 공모전 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, List<ContestResponse>>> getContests(@AuthUser Long userId) {
        List<ContestResponse> contests = contestService.getContests(userId);
        return ResponseEntity.ok(Map.of("contests", contests));
    }

    /**
     * 2. 공모전 등록
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addContest(
            @AuthUser Long userId,
            @Valid @RequestBody ContestCreateRequest request) {
        Long contestId = contestService.addContest(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "contestId", contestId,
                "message", "공모전이 등록되었습니다."
        ));
    }

    /**
     * 3. 공모전 수정
     */
    @PatchMapping("/{contestId}")
    public ResponseEntity<Map<String, Object>> updateContest(
            @AuthUser Long userId,
            @PathVariable Long contestId,
            @Valid @RequestBody ContestUpdateRequest request) {

        // 서비스 로직 실행 (수정 진행)
        contestService.updateContest(userId, contestId, request);

        // 명세서와 동일한 형태의 JSON 응답 생성
        return ResponseEntity.ok(Map.of(
                "message", "공모전 이력이 수정되었습니다.",
                "contestId", contestId,
                "result", request.getResult()
        ));
    }
    /**
     * 4. 공모전 삭제
     */
    @DeleteMapping("/{contestId}")
    public ResponseEntity<Map<String, String>> deleteContest(
            @AuthUser Long userId,
            @PathVariable Long contestId) {

        // 서비스 로직 실행 (데이터 삭제 진행)
        contestService.deleteContest(userId, contestId);

        // 명세서와 동일한 형태의 JSON 응답 생성
        return ResponseEntity.ok(Map.of(
                "message", "공모전 이력이 삭제되었습니다."
        ));
    }
}