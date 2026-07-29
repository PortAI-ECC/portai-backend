package com.portai.domain.project.service;

import com.portai.domain.project.dto.ProjectRequest;
import com.portai.domain.project.dto.ProjectResponse;
import com.portai.domain.project.entity.Project;
import com.portai.domain.project.repository.ProjectRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * 프로젝트 등록
     */
    @Transactional
    public ProjectResponse createProject(Long userId, ProjectRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Project project = Project.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .githubUrl(request.getGithubUrl())
                .build();

        Project saved = projectRepository.save(project);
        return new ProjectResponse(saved);
    }

    /**
     * 프로젝트 단건 조회
     */
    public ProjectResponse getProject(Long projectId) {
        Project project = findProjectOrThrow(projectId);
        return new ProjectResponse(project);
    }

    /**
     * 특정 유저의 프로젝트 목록 조회
     */
    public List<ProjectResponse> getMyProjects(Long userId) {
        return projectRepository.findAllByUserId(userId).stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 프로젝트 수정 (본인 소유만 가능)
     */
    @Transactional
    public ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest request) {
        Project project = findProjectOrThrow(projectId);
        validateOwner(project, userId);

        project.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getGithubUrl()
        );

        return new ProjectResponse(project);
    }

    /**
     * 프로젝트 삭제 (본인 소유만 가능)
     */
    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        Project project = findProjectOrThrow(projectId);
        validateOwner(project, userId);
        projectRepository.delete(project);
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private void validateOwner(Project project, Long userId) {
        if (!project.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }
}
