package com.portai.domain.profile.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String phone;
    private String introOneLiner;
    private String desiredJob;
    private String desiredCompany;

    private String profileImageUrl;
}