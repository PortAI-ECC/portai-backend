package com.portai.domain.profile.service;

import com.portai.domain.profile.dto.ProfileResponse;
import com.portai.domain.profile.dto.ProfileUpdateRequest;
import com.portai.domain.auth.entity.User;
import com.portai.domain.auth.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;

    /**
     * 1. 내 프로필 조회
     */
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .introOneLiner(user.getIntroOneLiner())
                .desiredJob(user.getDesiredJob())
                .desiredCompany(user.getDesiredCompany())
                .build();
    }

    /**
     * 2. 내 프로필 수정 (PATCH)
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티의 부분 수정 메서드 호출 (더티 체킹으로 자동 DB 반영)
        user.updateProfile(
                request.getPhone(),
                request.getIntroOneLiner(),
                request.getDesiredJob(),
                request.getDesiredCompany()
        );
    }
}