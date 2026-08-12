package com.portai.domain.education.service;

import com.portai.domain.education.dto.EducationRequest;
import com.portai.domain.education.dto.EducationResponse;
import com.portai.domain.education.entity.Education;
import com.portai.domain.education.repository.EducationRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
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
                request.getExpectedGraduation()
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