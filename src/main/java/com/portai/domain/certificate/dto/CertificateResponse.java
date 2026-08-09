package com.portai.domain.certificate.dto;

import com.portai.domain.certificate.entity.Certificate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CertificateResponse {

    private Long id;
    private String name;
    private String issuer;
    private LocalDate acquiredDate;
    private LocalDate expiryDate;
    private String score;

    public static CertificateResponse from(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .name(certificate.getName())
                .issuer(certificate.getIssuer())
                .acquiredDate(certificate.getAcquiredDate())
                .expiryDate(certificate.getExpiryDate())
                .score(certificate.getScore())
                .build();
    }
}