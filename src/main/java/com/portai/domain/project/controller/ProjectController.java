package com.portai.domain.project.controller;

import com.portai.domain.project.dto.ProjectRequest;
import com.portai.domain.project.dto.ProjectResponse;
import com.portai.domain.project.service.ProjectService;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.common.ApiResponse;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    // 프로젝트 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 프로젝트 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects() {
        List<ProjectResponse> responses = projectService.getMyProjects(getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 프로젝트 단건 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable Long projectId) {
        ProjectResponse response = projectService.getProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 프로젝트 수정
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.updateProject(getCurrentUserId(), projectId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 프로젝트 삭제
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(getCurrentUserId(), projectId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * JwtAuthenticationFilter가 SecurityContext에 심어둔 이메일로 현재 로그인한 유저의 id를 찾음.
     * auth 도메인이 main에 머지된 뒤에는 이 부분을 공통 유틸(예: @LoginUser 어노테이션)로 빼도 좋음.
     */
    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        return user.getId();
    }
}
