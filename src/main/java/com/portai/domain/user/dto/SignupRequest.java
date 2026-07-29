package com.portai.domain.user.dto;

import com.portai.domain.user.entity.DesiredJob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private DesiredJob desiredJob;
}