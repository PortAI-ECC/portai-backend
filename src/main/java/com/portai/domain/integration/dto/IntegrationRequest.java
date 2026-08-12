package com.portai.domain.integration.dto;

import com.portai.domain.integration.entity.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 플랫폼 연동 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
public class IntegrationRequest {

    @NotNull(message = "플랫폼 종류는 필수입니다.")
    private Platform platform;

    @NotBlank(message = "연동할 URL/계정 값은 필수입니다.")
    private String value;
}
