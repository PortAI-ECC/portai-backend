package com.portai.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class ProjectAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 첨부파일이 속한 프로젝트 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 파일 저장 경로/URL (LocalFileStorage 저장 결과)
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Builder
    public ProjectAttachment(Project project, String fileUrl) {
        this.project = project;
        this.fileUrl = fileUrl;
    }
}
