package com.portai.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private String message;

    @JsonProperty("accessToken") //명세서 글자 모양 고정
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("expiresIn")
    private long expiresIn;

    private UserInfo user;

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        @JsonProperty("userId")
        private Long userId;

        private String name;
        private String email;
    }
}