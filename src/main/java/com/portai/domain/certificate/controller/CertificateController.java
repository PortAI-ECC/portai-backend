package com.portai.domain.certificate.controller;

import com.portai.domain.certificate.dto.CertificateRequest;
import com.portai.domain.certificate.dto.CertificateResponse;
import com.portai.domain.certificate.service.CertificateService;
import com.portai.global.annotation.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    // 자격증 목록 조회
    @GetMapping
    public List<CertificateResponse> getCertificates(
            @AuthUser Long userId
    ) {
        return certificateService.getMyCertificates(userId);
    }

    // 자격증 등록
    @PostMapping
    public CertificateResponse createCertificate(
            @AuthUser Long userId,
            @Valid @RequestBody CertificateRequest request
    ) {
        return certificateService.createCertificate(userId, request);
    }

    // 자격증 수정
    @PatchMapping("/{certId}")
    public CertificateResponse updateCertificate(
            @AuthUser Long userId,
            @PathVariable Long certId,
            @Valid @RequestBody CertificateRequest request
    ) {
        return certificateService.updateCertificate(
                userId,
                certId,
                request
        );
    }

    // 자격증 삭제
    @DeleteMapping("/{certId}")
    public void deleteCertificate(
            @AuthUser Long userId,
            @PathVariable Long certId
    ) {
        certificateService.deleteCertificate(
                userId,
                certId
        );
    }
}