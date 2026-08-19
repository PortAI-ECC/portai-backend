package com.portai.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LogoutRequest {
    private String refreshToken; // 프론트엔드가 보내주는 리프레시 토큰
}