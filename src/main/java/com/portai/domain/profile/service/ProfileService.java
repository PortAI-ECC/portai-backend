package com.portai.domain.profile.service;

import com.portai.domain.profile.dto.ProfileResponse;
import com.portai.domain.profile.dto.ProfileUpdateRequest;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.global.util.LocalFileStorage; // 팀원이 만든 유틸리티 import!
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final LocalFileStorage localFileStorage; //  로컬 스토리지 주입받기

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
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    /**
     * 2. 내 프로필 수정 (PATCH)
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.getPhone(),
                request.getIntroOneLiner(),
                request.getDesiredJob(),
                request.getDesiredCompany(),
                request.getProfileImageUrl()
        );
    }

    /**
     * 3. 프로필 이미지 업로드 (POST)
     */
    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_FILE);
        }

        // 2. 더미 URL 대신 팀원이 만든 로컬 스토리지에 파일 저장! (서브 폴더명: profiles)
        String storedFilePath = localFileStorage.store(file, "profiles");

        // 3. 엔티티에 파일 경로 업데이트 (예: uploads/profiles/1234-uuid_이미지.png)
        user.updateProfileImage(storedFilePath);

        // 4. 프론트엔드로 경로 반환
        return storedFilePath;
    }
}