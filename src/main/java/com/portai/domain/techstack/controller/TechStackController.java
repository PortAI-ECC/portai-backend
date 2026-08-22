package com.portai.domain.techstack.controller;

import com.portai.domain.techstack.dto.TechStackCreateRequest;
import com.portai.domain.techstack.dto.TechStackReorderRequest;
import com.portai.domain.techstack.dto.TechStackResponse;
import com.portai.domain.techstack.dto.TechStackUpdateRequest;
import com.portai.domain.techstack.service.TechStackService;
import com.portai.global.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 프론트엔드와 통신하는 기술 스택 API 컨트롤러
 */
@RestController
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {

    private final TechStackService techStackService;

    /**
     * 1. 기술 스택 목록 조회
     * [GET] /api/tech-stacks
     */
    @GetMapping
    public ResponseEntity<Map<String, List<TechStackResponse>>> getTechStacks(
            @AuthUser Long userId) {

        List<TechStackResponse> techStacks = techStackService.getTechStacks(userId);

        // Map.of()를 사용하여 불필요한 코드를 줄이고 깔끔하게 포장
        return ResponseEntity.ok(Map.of("techStacks", techStacks));
    }

    /**
     * 2. 기술 스택 개별 추가
     * [POST] /api/tech-stacks
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addTechStack(
            @AuthUser Long userId,
            @RequestBody TechStackCreateRequest request) {

        Long skillId = techStackService.addTechStack(userId, request);

        // Map.of() 활용 및 성공 메시지 포장
        Map<String, Object> response = Map.of(
                "skillId", skillId,
                "message", "기술스택이 추가되었습니다."
        );

        // 자원이 새롭게 생성되었으므로 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 3. 기술 스택 개별 삭제
     * [DELETE] /api/tech-stacks/{skillId}
     */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteTechStack(
            @AuthUser Long userId,
            @PathVariable Long skillId) {

        techStackService.deleteTechStack(userId, skillId);

        // 반환할 데이터가 없으므로 제네릭 타입을 <Void>로 명시
        return ResponseEntity.ok().build();
    }
    /**
     * 4. 기술 스택 개별 수정
     * [PATCH] /api/tech-stacks/{skillId}
     */
    @PatchMapping("/{skillId}")
    public ResponseEntity<Void> updateTechStack(
            @AuthUser Long userId,
            @PathVariable Long skillId,
            @RequestBody TechStackUpdateRequest request) {

        techStackService.updateTechStack(userId, skillId, request);

        // 성공 시 데이터 없이 상태 코드 200만 반환
        return ResponseEntity.ok().build();
    }

    /**
     * 5. 기술 스택 순서 재정렬
     * [PUT] /api/tech-stacks/reorder
     */
    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderTechStacks(
            @AuthUser Long userId,
            @RequestBody TechStackReorderRequest request) {

        techStackService.reorderTechStacks(userId, request);

        // 성공 시 데이터 없이 상태 코드 200만 반환
        return ResponseEntity.ok().build();
    }
}