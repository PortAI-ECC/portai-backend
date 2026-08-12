package com.portai.domain.project.repository;

import com.portai.domain.project.entity.ProjectAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAttachmentRepository extends JpaRepository<ProjectAttachment, Long> {

    // 특정 프로젝트에 등록된 발표자료 목록 조회
    List<ProjectAttachment> findAllByProjectId(Long projectId);
}
