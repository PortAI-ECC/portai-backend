package com.portai.domain.profile.dto;

import com.portai.domain.auth.entity.DesiredJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private Long userId; // 명세서에 맞춰 추가된 부분!
    private String name;
    private String email;
    private String phone;
    private String introOneLiner;
    private DesiredJob desiredJob;
    private String desiredCompany;
}