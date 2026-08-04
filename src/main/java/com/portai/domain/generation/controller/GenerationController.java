package com.portai.domain.generation.controller;

import com.portai.domain.generation.dto.GenerationRequest;
import com.portai.domain.generation.dto.GenerationResponse;
import com.portai.domain.generation.dto.GenerationResultResponse;
import com.portai.domain.generation.dto.GenerationResultUpdateRequest;
import com.portai.domain.generation.entity.GenerationResultType;
import com.portai.domain.generation.service.GenerationService;
import com.portai.global.annotation.AuthUser;
import com.portai.global.common.ApiResponse;
import com.portai.global.util.LocalFileStorage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/generations")
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;
    private final LocalFileStorage fileStorage;

    // 결과물 생성 요청 (이력서/포트폴리오/자소서 등 유형 여러 개 동시 요청 가능)
    @PostMapping
    public ResponseEntity<ApiResponse<GenerationResponse>> createGeneration(
            @AuthUser Long userId,
            @Valid @RequestBody GenerationRequest request) {
        GenerationResponse response = generationService.createGeneration(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 동일 조건 재생성 (기존 이력의 jobPosting/style/유형을 그대로 이어받아 새 이력 생성)
    @PostMapping("/{generationId}/regenerate")
    public ResponseEntity<ApiResponse<GenerationResponse>> regenerate(
            @AuthUser Long userId,
            @PathVariable Long generationId) {
        GenerationResponse response = generationService.regenerate(userId, generationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 생성 이력 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<GenerationResponse>>> getMyGenerations(@AuthUser Long userId) {
        List<GenerationResponse> responses = generationService.getMyGenerations(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 생성 결과 상세 조회 (결과물 목록 포함)
    @GetMapping("/{generationId}")
    public ResponseEntity<ApiResponse<GenerationResponse>> getGeneration(
            @AuthUser Long userId,
            @PathVariable Long generationId) {
        GenerationResponse response = generationService.getGeneration(userId, generationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 생성 결과물 중 특정 유형을 사용자가 직접 수정
    @PatchMapping("/{generationId}/results/{type}")
    public ResponseEntity<ApiResponse<GenerationResponse>> editResult(
            @AuthUser Long userId,
            @PathVariable Long generationId,
            @PathVariable GenerationResultType type,
            @Valid @RequestBody GenerationResultUpdateRequest request) {
        GenerationResponse response = generationService.editResult(userId, generationId, type, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 결과물 파일 다운로드
    // TODO: infra/llmclient 연동 후 실제 생성 파일(fileUrl)이 채워지면 그 파일을 그대로 내려주고,
    //       지금처럼 content만 있는 경우엔 텍스트 파일로 변환해서 내려주는 fallback만 유지하면 됨
    @GetMapping("/{generationId}/results/{type}/download")
    public ResponseEntity<Resource> downloadResult(
            @AuthUser Long userId,
            @PathVariable Long generationId,
            @PathVariable GenerationResultType type) {

        GenerationResultResponse result = generationService.getResultForDownload(userId, generationId, type);

        Resource resource;
        String filename;

        if (result.getFileUrl() != null) {
            resource = fileStorage.loadAsResource(result.getFileUrl());
            filename = Paths.get(result.getFileUrl()).getFileName().toString();
        } else {
            String content = result.getContent() != null ? result.getContent() : "";
            resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
            filename = type.name().toLowerCase() + "_" + generationId + ".txt";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // 생성 이력 삭제
    @DeleteMapping("/{generationId}")
    public ResponseEntity<ApiResponse<Void>> deleteGeneration(
            @AuthUser Long userId,
            @PathVariable Long generationId) {
        generationService.deleteGeneration(userId, generationId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
