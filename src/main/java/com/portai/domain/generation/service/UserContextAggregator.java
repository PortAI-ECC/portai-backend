package com.portai.domain.generation.service;

import com.portai.domain.activity.entity.Activity;
import com.portai.domain.activity.repository.ActivityRepository;
import com.portai.domain.career.entity.Career;
import com.portai.domain.career.repository.CareerRepository;
import com.portai.domain.certificate.entity.Certificate;
import com.portai.domain.certificate.repository.CertificateRepository;
import com.portai.domain.contest.entity.Contest;
import com.portai.domain.contest.repository.ContestRepository;
import com.portai.domain.education.entity.Education;
import com.portai.domain.education.repository.EducationRepository;
import com.portai.domain.project.entity.Project;
import com.portai.domain.project.repository.ProjectRepository;
import com.portai.domain.techstack.entity.TechStack;
import com.portai.domain.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사용자의 경험 데이터를 모아서 LLM 프롬프트용 텍스트로 변환한다.
 */
@Component
@RequiredArgsConstructor
public class UserContextAggregator {

    private final ProjectRepository projectRepository;
    private final CareerRepository careerRepository;
    private final ContestRepository contestRepository;
    private final TechStackRepository techStackRepository;
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;
    private final ActivityRepository activityRepository;

    public String buildUserContext(Long userId) {

        StringBuilder sb = new StringBuilder();

        // === [프로젝트 데이터] ===
        List<Project> projects = projectRepository.findAllByUserId(userId);

        for (Project p : projects) {
            sb.append("- [프로젝트] ").append(p.getTitle());

            if (p.getDescription() != null && !p.getDescription().isBlank()) {
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
        List<Career> careers =
                careerRepository.findByUserIdOrderByIdDesc(userId);

        for (Career c : careers) {
            sb.append("- [인턴/경력] ")
                    .append(c.getCompanyName());

            if (c.getPosition() != null && !c.getPosition().isBlank()) {
                sb.append(" (").append(c.getPosition()).append(")");
            }

            if (c.getDuties() != null && !c.getDuties().isBlank()) {
                sb.append(" / 주요업무: ").append(c.getDuties());
            }

            if (c.getAchievements() != null
                    && !c.getAchievements().isBlank()) {
                sb.append(" / 성과: ").append(c.getAchievements());
            }

            if (c.getFreeText() != null && !c.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(c.getFreeText());
            }

            sb.append("\n");
        }

        // === [공모전 데이터] ===
        List<Contest> contests =
                contestRepository.findByUserIdOrderByIdDesc(userId);

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

        // === [기술스택 데이터] ===
        List<TechStack> techStacks =
                techStackRepository.findByUserIdOrderByOrderIndexAsc(userId);

        for (TechStack t : techStacks) {
            sb.append("- [기술스택] ").append(t.getName());

            if (t.getProficiency() != null) {
                sb.append(" / 숙련도: ")
                        .append(t.getProficiency().name());
            }

            if (t.getFreeText() != null && !t.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(t.getFreeText());
            }

            sb.append("\n");
        }

        // === [학력 데이터] ===
        List<Education> educations =
                educationRepository.findAllByUserId(userId);

        for (Education e : educations) {
            sb.append("- [학력] ").append(e.getSchool());

            if (e.getDegree() != null) {
                sb.append(" / 학위: ").append(e.getDegree().name());
            }

            if (e.getMajor() != null && !e.getMajor().isBlank()) {
                sb.append(" / 전공: ").append(e.getMajor());
            }

            if (e.getDoubleMajor() != null
                    && !e.getDoubleMajor().isBlank()) {
                sb.append(" / 복수전공: ").append(e.getDoubleMajor());
            }

            if (e.getGpaScore() != null) {
                sb.append(" / 학점: ").append(e.getGpaScore());

                if (e.getGpaScale() != null) {
                    sb.append(" / ").append(e.getGpaScale());
                }
            }

            if (e.getStatus() != null) {
                sb.append(" / 상태: ").append(e.getStatus().name());
            }

            if (e.getExpectedGraduation() != null) {
                sb.append(" / 졸업 예정일: ")
                        .append(e.getExpectedGraduation());
            }

            if (e.getFreeText() != null && !e.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(e.getFreeText());
            }

            sb.append("\n");
        }

        // === [자격증 데이터] ===
        List<Certificate> certificates =
                certificateRepository.findAllByUserId(userId);

        for (Certificate c : certificates) {
            sb.append("- [자격증] ").append(c.getName());

            if (c.getIssuer() != null && !c.getIssuer().isBlank()) {
                sb.append(" / 발급기관: ").append(c.getIssuer());
            }

            if (c.getAcquiredDate() != null) {
                sb.append(" / 취득일: ").append(c.getAcquiredDate());
            }

            if (c.getExpiryDate() != null) {
                sb.append(" / 만료일: ").append(c.getExpiryDate());
            }

            if (c.getScore() != null && !c.getScore().isBlank()) {
                sb.append(" / 점수 또는 등급: ").append(c.getScore());
            }

            if (c.getFreeText() != null && !c.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(c.getFreeText());
            }

            sb.append("\n");
        }

        // === [활동이력 데이터] ===
        List<Activity> activities =
                activityRepository.findAllByUserId(userId);

        for (Activity a : activities) {
            sb.append("- [활동이력] ").append(a.getName());

            if (a.getStartDate() != null) {
                sb.append(" / 기간: ").append(a.getStartDate());

                if (a.getEndDate() != null) {
                    sb.append(" ~ ").append(a.getEndDate());
                }
            }

            if (a.getRole() != null && !a.getRole().isBlank()) {
                sb.append(" / 역할: ").append(a.getRole());
            }

            if (a.getDescription() != null
                    && !a.getDescription().isBlank()) {
                sb.append(" / 활동 설명: ").append(a.getDescription());
            }

            if (a.getFreeText() != null && !a.getFreeText().isBlank()) {
                sb.append(" / 상세내용: ").append(a.getFreeText());
            }

            sb.append("\n");
        }

        if (sb.isEmpty()) {
            sb.append("(등록된 경험 데이터가 없습니다.)");
        }

        return sb.toString();
    }
}