package com.portai.domain.project.service;

import com.portai.domain.project.dto.ProjectAttachmentResponse;
import com.portai.domain.project.dto.ProjectRequest;
import com.portai.domain.project.dto.ProjectResponse;
import com.portai.domain.project.entity.Project;
import com.portai.domain.project.entity.ProjectAttachment;
import com.portai.domain.project.repository.ProjectAttachmentRepository;
import com.portai.domain.project.repository.ProjectRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.global.util.LocalFileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final String ATTACHMENT_UPLOAD_SUB_DIR = "project-attachments";

    private final ProjectRepository projectRepository;
    private final ProjectAttachmentRepository projectAttachmentRepository;
    private final UserRepository userRepository;
    private final LocalFileStorage fileStorage;

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

    /**
     * 발표자료 업로드 (본인 소유 프로젝트만 가능)
     */
    @Transactional
    public ProjectAttachmentResponse uploadAttachment(Long userId, Long projectId, MultipartFile file) {
        Project project = findProjectOrThrow(projectId);
        validateOwner(project, userId);

        String storedPath = fileStorage.store(file, ATTACHMENT_UPLOAD_SUB_DIR);

        ProjectAttachment attachment = ProjectAttachment.builder()
                .project(project)
                .fileUrl(storedPath)
                .build();

        return new ProjectAttachmentResponse(projectAttachmentRepository.save(attachment));
    }

    /**
     * AI 설명 생성 요청 (본인 소유 프로젝트만 가능)
     * TODO: infra/llmclient 연동 후 프로젝트의 my_contribution/proudest_achievement 등을 바탕으로
     *       실제 LLM 호출 결과를 반영하도록 교체 (지금은 자리표시자 문구만 채움)
     */
    @Transactional
    public ProjectResponse generateDescription(Long userId, Long projectId) {
        Project project = findProjectOrThrow(projectId);
        validateOwner(project, userId);

        String placeholder = "AI 설명 생성 준비 중입니다. (infra/llmclient 연동 후 자동으로 채워질 예정)";
        project.applyGeneratedDescription(placeholder);

        return new ProjectResponse(project);
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
