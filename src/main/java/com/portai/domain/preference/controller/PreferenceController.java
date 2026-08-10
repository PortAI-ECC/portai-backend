package com.portai.domain.preference.controller;

import com.portai.domain.preference.dto.PreferenceResponse;
import com.portai.domain.preference.dto.PreferenceUpdateRequest;
import com.portai.domain.preference.service.PreferenceService;
import com.portai.global.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    /**
     * 1. 맞춤화 설정 조회
     */
    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreference(@AuthUser Long userId) {
        // 배열(List)로 감싸지 않고 명세서대로 객체 자체를 바로 응답으로 내보냅니다.
        PreferenceResponse response = preferenceService.getPreference(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 맞춤화 설정 수정
     */
    @PatchMapping
    public ResponseEntity<Map<String, Object>> updatePreference(
            @AuthUser Long userId,
            @RequestBody PreferenceUpdateRequest request) {

        preferenceService.updatePreference(userId, request);

        // 클라이언트가 보낸 데이터만 예쁘게 묶어서 응답
        Map<String, Object> response = new HashMap<>();
        response.put("message", "맞춤화 설정이 수정되었습니다.");

        if (request.getKeywords() != null) response.put("keywords", request.getKeywords());
        if (request.getEmphasizedTypes() != null) response.put("emphasizedTypes", request.getEmphasizedTypes());
        if (request.getStyle() != null) response.put("style", request.getStyle());

        return ResponseEntity.ok(response);
    }
}