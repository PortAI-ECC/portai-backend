package com.portai.domain.career.controller;

import com.portai.domain.career.dto.CareerCreateRequest;
import com.portai.domain.career.dto.CareerResponse;
import com.portai.domain.career.dto.CareerUpdateRequest;
import com.portai.domain.career.service.CareerService;
import com.portai.global.annotation.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    /**
     * 1. 인턴/경력 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, List<CareerResponse>>> getCareers(@AuthUser Long userId) {
        List<CareerResponse> careers = careerService.getCareers(userId);
        return ResponseEntity.ok(Map.of("careers", careers));
    }

    /**
     * 2. 인턴/경력 등록
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addCareer(
            @AuthUser Long userId,
            @Valid @RequestBody CareerCreateRequest request) { // POST는 필수값 검사(@Valid) 수행

        Long careerId = careerService.addCareer(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "careerId", careerId,
                "message", "경력이 등록되었습니다."
        ));
    }

    /**
     * 3. 인턴/경력 수정 (PATCH)
     */
    @PatchMapping("/{careerId}")
    public ResponseEntity<Map<String, Object>> updateCareer(
            @AuthUser Long userId,
            @PathVariable Long careerId,
            @RequestBody CareerUpdateRequest request) { // PATCH는 부분 수정이므로 @Valid 생략

        careerService.updateCareer(userId, careerId, request);

        // 명세서처럼 수정된 항목만 응답으로 돌려주기 위한 동적 Map 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "경력이 수정되었습니다.");
        response.put("careerId", careerId);

        if (request.getCompanyName() != null) response.put("companyName", request.getCompanyName());
        if (request.getPosition() != null) response.put("position", request.getPosition());
        if (request.getStartDate() != null) response.put("startDate", request.getStartDate());
        if (request.getEndDate() != null) response.put("endDate", request.getEndDate());
        if (request.getDuties() != null) response.put("duties", request.getDuties());
        if (request.getAchievements() != null) response.put("achievements", request.getAchievements());
        if (request.getFreeText() != null) response.put("freeText", request.getFreeText());

        return ResponseEntity.ok(response);
    }

    /**
     * 4. 인턴/경력 삭제
     */
    @DeleteMapping("/{careerId}")
    public ResponseEntity<Map<String, String>> deleteCareer(
            @AuthUser Long userId,
            @PathVariable Long careerId) {

        careerService.deleteCareer(userId, careerId);

        return ResponseEntity.ok(Map.of(
                "message", "경력이 삭제되었습니다."
        ));
    }
}