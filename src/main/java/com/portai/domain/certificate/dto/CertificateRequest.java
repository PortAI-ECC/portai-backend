package com.portai.domain.certificate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CertificateRequest {

    @NotBlank(message = "자격증명은 필수입니다.")
    private String name;

    private String issuer;

    private LocalDate acquiredDate;

    private LocalDate expiryDate;

    private String score;
}