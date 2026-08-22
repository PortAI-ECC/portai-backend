package com.portai.domain.project.dto;

import com.portai.domain.project.entity.ProjectAttachment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectAttachmentResponse {

    private final Long id;
    private final String fileUrl;
    private final LocalDateTime uploadedAt;

    public ProjectAttachmentResponse(ProjectAttachment attachment) {
        this.id = attachment.getId();
        this.fileUrl = attachment.getFileUrl();
        this.uploadedAt = attachment.getUploadedAt();
    }
}
