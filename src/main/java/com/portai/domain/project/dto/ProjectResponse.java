package com.portai.domain.project.dto;

import com.portai.domain.project.entity.Project;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 프로젝트 조회 응답 DTO
 */
@Getter
public class ProjectResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String githubUrl;
    private final LocalDateTime createdAt;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.githubUrl = project.getGithubUrl();
        this.createdAt = project.getCreatedAt();
    }
}
