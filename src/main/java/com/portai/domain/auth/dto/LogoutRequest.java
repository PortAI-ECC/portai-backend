package com.portai.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {
    private String refreshToken; // 프론트엔드가 보내주는 리프레시 토큰
}