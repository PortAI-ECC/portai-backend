package com.portai.domain.generation.service;

import com.portai.domain.project.entity.Project;
import com.portai.domain.project.repository.ProjectRepository;
//경력
import com.portai.domain.career.entity.Career;
import com.portai.domain.career.repository.CareerRepository;
//공모전
import com.portai.domain.contest.entity.Contest;
import com.portai.domain.contest.repository.ContestRepository;
//기술스택
import com.portai.domain.techstack.entity.TechStack;
import com.portai.domain.techstack.repository.TechStackRepository;

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
    private final CareerRepository careerRepository; // Career
    private final ContestRepository contestRepository; //contest
    private final TechStackRepository techStackRepository; //tech-stack

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

        // === [인턴/경력 데이터] ===
        List<Career> careers = careerRepository.findByUserIdOrderByIdDesc(userId);

        for (Career c : careers) {
            sb.append("- [인턴/경력] ").append(c.getCompanyName()).append(" (").append(c.getPosition()).append(")");

            if (c.getDuties() != null && !c.getDuties().isBlank()) {
                sb.append(" / 주요업무: ").append(c.getDuties());
            }
            if (c.getAchievements() != null && !c.getAchievements().isBlank()) {
                sb.append(" / 성과: ").append(c.getAchievements());
            }
            if (c.getFreeText() != null && !c.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(c.getFreeText());
            }
            sb.append("\n");
        }

        // === [공모전 데이터] ===
        List<Contest> contests = contestRepository.findByUserIdOrderByIdDesc(userId);
        for (Contest c : contests) {
            sb.append("- [공모전] ").append(c.getName());

            if (c.getHost() != null && !c.getHost().isBlank()) {
                sb.append(" / 주최: ").append(c.getHost());
            }
            if (c.getRole() != null && !c.getRole().isBlank()) {
                sb.append(" / 역할: ").append(c.getRole());
            }
            if (c.getResult() != null && !c.getResult().isBlank()) {
                sb.append(" / 결과: ").append(c.getResult());
            }
            if (c.getFreeText() != null && !c.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(c.getFreeText());
            }
            sb.append("\n");
        }
        // === [기술 스택 데이터] ===
        // 사용자가 설정한 순서(orderIndexAsc)대로 텍스트에 담김
        List<TechStack> techStacks = techStackRepository.findByUserIdOrderByOrderIndexAsc(userId);
        for (TechStack t : techStacks) {
            sb.append("- [기술스택] ").append(t.getName());

            if (t.getProficiency() != null) {
                sb.append(" / 숙련도: ").append(t.getProficiency().name());
            }
            if (t.getFreeText() != null && !t.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(t.getFreeText());
            }
            sb.append("\n");
        }

        if (sb.isEmpty()) {
            sb.append("(등록된 경험 데이터가 없습니다.)");
        }

        return sb.toString();
    }
}