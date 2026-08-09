package com.portai.domain.education.controller;

import com.portai.domain.education.dto.EducationRequest;
import com.portai.domain.education.dto.EducationResponse;
import com.portai.domain.education.service.EducationService;
import com.portai.global.annotation.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/education")
public class EducationController {

    private final EducationService educationService;

    // 교육 목록 조회
    @GetMapping
    public List<EducationResponse> getMyEducation(
            @AuthUser Long userId
    ) {
        return educationService.getMyEducation(userId);
    }

    // 교육 등록
    @PostMapping
    public EducationResponse createEducation(
            @AuthUser Long userId,
            @Valid @RequestBody EducationRequest request
    ) {
        return educationService.createEducation(userId, request);
    }

    // 교육 수정
    @PatchMapping("/{eduId}")
    public EducationResponse updateEducation(
            @AuthUser Long userId,
            @PathVariable Long eduId,
            @Valid @RequestBody EducationRequest request
    ) {
        return educationService.updateEducation(
                userId,
                eduId,
                request
        );
    }

    // 교육 삭제
    @DeleteMapping("/{eduId}")
    public void deleteEducation(
            @AuthUser Long userId,
            @PathVariable Long eduId
    ) {
        educationService.deleteEducation(userId, eduId);
    }
}