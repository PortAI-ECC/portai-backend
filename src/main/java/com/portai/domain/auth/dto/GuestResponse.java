package com.portai.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuestResponse {

    private String message;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("expiresIn")
    private long expiresIn;
}