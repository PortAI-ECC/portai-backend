package com.portai.domain.profile.dto;

import com.portai.domain.auth.entity.DesiredJob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String phone;
    private String introOneLiner;
    private DesiredJob desiredJob;
    private String desiredCompany;
}