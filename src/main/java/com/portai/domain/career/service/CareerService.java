package com.portai.domain.career.service;

import com.portai.domain.career.dto.CareerCreateRequest;
import com.portai.domain.career.dto.CareerResponse;
import com.portai.domain.career.dto.CareerUpdateRequest;
import com.portai.domain.career.entity.Career;
import com.portai.domain.career.repository.CareerRepository;
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
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;
    private final UserRepository userRepository;

    /**
     * 1. 인턴/경력 목록 조회 (GET)
     */
    public List<CareerResponse> getCareers(Long userId) {
        List<Career> careers = careerRepository.findByUserIdOrderByIdDesc(userId);

        return careers.stream()
                .map(CareerResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. 인턴/경력 등록 (POST)
     */
    @Transactional
    public Long addCareer(Long userId, CareerCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Career career = Career.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .position(request.getPosition())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .duties(request.getDuties())
                .achievements(request.getAchievements())
                .build();

        return careerRepository.save(career).getId();
    }

    /**
     * 3. 인턴/경력 수정 (PATCH) - 부분 수정
     */
    @Transactional
    public void updateCareer(Long userId, Long careerId, CareerUpdateRequest request) {
        Career career = careerRepository.findByIdAndUserId(careerId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CAREER_NOT_FOUND));

        // 값이 들어온 항목만 덮어쓰도록 엔티티의 메서드 호출
        career.updateCareer(
                request.getCompanyName(),
                request.getPosition(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDuties(),
                request.getAchievements()
        );
    }

    /**
     * 4. 인턴/경력 삭제 (DELETE)
     */
    @Transactional
    public void deleteCareer(Long userId, Long careerId) {
        Career career = careerRepository.findByIdAndUserId(careerId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CAREER_NOT_FOUND));

        careerRepository.delete(career);
    }
}