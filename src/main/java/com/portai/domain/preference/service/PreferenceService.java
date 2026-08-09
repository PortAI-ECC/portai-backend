package com.portai.domain.preference.service;

import com.portai.domain.preference.dto.PreferenceResponse;
import com.portai.domain.preference.dto.PreferenceUpdateRequest;
import com.portai.domain.preference.entity.Preference;
import com.portai.domain.preference.repository.PreferenceRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    /**
     * 1. 맞춤화 설정 조회 (GET)
     */
    public PreferenceResponse getPreference(Long userId) {
        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREFERENCE_NOT_FOUND));

        return PreferenceResponse.from(preference);
    }

    /**
     * 2. 맞춤화 설정 수정 (PATCH) - 없으면 최초 생성 (Upsert)
     */
    @Transactional
    public void updatePreference(Long userId, PreferenceUpdateRequest request) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 기존 설정이 있으면 가져오고, 없으면 텅 빈 엔티티 새로 만들기
        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> Preference.builder()
                        .user(user)
                        .build());

        // 3. 값이 들어온 항목만 덮어쓰기 (엔티티 내부의 if null 체크 로직 작동)
        preference.updatePreference(
                request.getKeywords(),
                request.getEmphasizedTypes(),
                request.getStyle()
        );

        // 4. DB에 저장 (최초 생성인 경우 insert, 기존 수정인 경우 update가 자동으로 나갑니다)
        preferenceRepository.save(preference);
    }
}