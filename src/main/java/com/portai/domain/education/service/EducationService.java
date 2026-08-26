package com.portai.domain.education.service;

import com.portai.domain.education.dto.EducationRequest;
import com.portai.domain.education.dto.EducationResponse;
import com.portai.domain.education.entity.Education;
import com.portai.domain.education.repository.EducationRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.infra.llmclient.LlmClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final LlmClient llmClient;

    // 교육 이력 등록
    @Transactional
    public EducationResponse createEducation(Long userId, EducationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Education education = Education.builder()
                .user(user)
                .school(request.getSchool())
                .degree(request.getDegree())
                .major(request.getMajor())
                .doubleMajor(request.getDoubleMajor())
                .gpaScore(request.getGpaScore())
                .gpaScale(request.getGpaScale())
                .status(request.getStatus())
                .expectedGraduation(request.getExpectedGraduation())
                .freeText(request.getFreeText())
                .build();

        Education saved = educationRepository.save(education);

        return EducationResponse.from(saved);
    }

    // 현재 사용자의 교육 이력 목록 조회
    public List<EducationResponse> getMyEducation(Long userId) {

        return educationRepository.findAllByUserId(userId)
                .stream()
                .map(EducationResponse::from)
                .collect(Collectors.toList());
    }

    // 교육 이력 수정
    @Transactional
    public EducationResponse updateEducation(
            Long userId,
            Long eduId,
            EducationRequest request
    ) {

        Education education = findEducationOrThrow(eduId);

        validateOwner(education, userId);

        education.update(
                request.getSchool(),
                request.getDegree(),
                request.getMajor(),
                request.getDoubleMajor(),
                request.getGpaScore(),
                request.getGpaScale(),
                request.getStatus(),
                request.getExpectedGraduation(),
                request.getFreeText()
        );

        return EducationResponse.from(education);
    }

    // 교육 이력 삭제
    @Transactional
    public void deleteEducation(Long userId, Long eduId) {

        Education education = findEducationOrThrow(eduId);

        validateOwner(education, userId);

        educationRepository.delete(education);
    }

    /**
     * 학력 AI 초안 생성
     */
    @Transactional(readOnly = true)
    public String generateEducationDescription(Long userId, Long eduId) {

        Education education = findEducationOrThrow(eduId);

        validateOwner(education, userId);

        String prompt = String.format(
                "너는 전문 이력서 컨설턴트야. 다음 학력 데이터를 바탕으로 포트폴리오에 들어갈 3~4줄짜리 역량 중심 요약 초안을 작성해줘.\n" +
                        "- 학교명: %s\n" +
                        "- 학위: %s\n" +
                        "- 전공: %s\n" +
                        "- 복수전공: %s\n" +
                        "- 학점: %s / %s\n" +
                        "- 재학 상태: %s\n" +
                        "- 졸업 예정일: %s\n" +
                        "- 작성한 메모(자유텍스트): %s",
                education.getSchool(),
                education.getDegree() != null
                        ? education.getDegree().name()
                        : "없음",
                education.getMajor() != null
                        ? education.getMajor()
                        : "없음",
                education.getDoubleMajor() != null
                        ? education.getDoubleMajor()
                        : "없음",
                education.getGpaScore() != null
                        ? education.getGpaScore().toString()
                        : "없음",
                education.getGpaScale() != null
                        ? education.getGpaScale().toString()
                        : "없음",
                education.getStatus() != null
                        ? education.getStatus().name()
                        : "없음",
                education.getExpectedGraduation() != null
                        ? education.getExpectedGraduation().toString()
                        : "없음",
                education.getFreeText() != null
                        ? education.getFreeText()
                        : "없음"
        );

        return llmClient.generateText(prompt);
    }

    private Education findEducationOrThrow(Long eduId) {

        return educationRepository.findById(eduId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.EDUCATION_NOT_FOUND));
    }

    private void validateOwner(Education education, Long userId) {

        if (!education.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.EDUCATION_ACCESS_DENIED);
        }
    }
}