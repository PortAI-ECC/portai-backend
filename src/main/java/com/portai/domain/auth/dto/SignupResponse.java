package com.portai.domain.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long userId;
    private String message;
    private String name;
    private String email;
}