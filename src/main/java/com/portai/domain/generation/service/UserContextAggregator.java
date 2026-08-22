package com.portai.domain.generation.service;

import com.portai.domain.project.entity.Project;
import com.portai.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사용자의 경험 데이터(현재는 projects만)를 모아서 LLM 프롬프트용 텍스트로 변환한다.
 * TODO: activities, careers, certificates, education, tech_stacks 도 같은 패턴으로 추가
 *       (해당 리포지토리들은 윤지/가현 담당 도메인에 있음)
 */
@Component
@RequiredArgsConstructor
public class UserContextAggregator {

    private final ProjectRepository projectRepository;

    public String buildUserContext(Long userId) {
        StringBuilder sb = new StringBuilder();

        List<Project> projects = projectRepository.findAllByUserId(userId);
        for (Project p : projects) {
            sb.append("- [프로젝트] ").append(p.getTitle());
            if (p.getDescription() != null) {
                sb.append(" / 설명: ").append(p.getDescription());
            }
            if (p.getStartDate() != null) {
                sb.append(" / 기간: ").append(p.getStartDate());
                if (p.getEndDate() != null) {
                    sb.append(" ~ ").append(p.getEndDate());
                }
            }
            sb.append("\n");
        }

        if (sb.isEmpty()) {
            sb.append("(등록된 경험 데이터가 없습니다.)");
        }

        return sb.toString();
    }
}