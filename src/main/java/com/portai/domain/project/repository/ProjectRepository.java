package com.portai.domain.project.repository;

import com.portai.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 특정 유저가 등록한 프로젝트 목록 조회
    List<Project> findAllByUserId(Long userId);
}
