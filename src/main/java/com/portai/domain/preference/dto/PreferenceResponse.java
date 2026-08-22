package com.portai.domain.preference.dto;

import com.portai.domain.preference.entity.Preference;
import com.portai.domain.preference.entity.Style;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PreferenceResponse {

    private List<String> keywords;
    private List<String> emphasizedTypes;
    private Style style;

    // 엔티티를 DTO로 변환해주는 편의 메서드
    public static PreferenceResponse from(Preference preference) {
        return PreferenceResponse.builder()
                .keywords(preference.getKeywords())
                .emphasizedTypes(preference.getEmphasizedTypes())
                .style(preference.getStyle())
                .build();
    }
}