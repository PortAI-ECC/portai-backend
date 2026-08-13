package com.portai.domain.profile.controller;

import com.portai.domain.profile.dto.ProfileResponse;
import com.portai.domain.profile.dto.ProfileUpdateRequest;
import com.portai.domain.profile.service.ProfileService;
import com.portai.global.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 내 프로필 조회
     * GET /api/profile
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthUser Long userId) {
        // 서비스에서 유저 엔티티를 조회한 뒤 ProfileResponse DTO로 변환하여 반환
        ProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 프로필 수정 (부분 수정)
     * PATCH /api/profile
     */
    @PatchMapping
    public ResponseEntity<Map<String, String>> updateMyProfile(
            @AuthUser Long userId,
            @RequestBody ProfileUpdateRequest request) {

        // 서비스 로직 실행 (수정 진행)
        profileService.updateProfile(userId, request);

        // 명세서와 동일하게 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "프로필이 성공적으로 수정되었습니다."));
    }
}