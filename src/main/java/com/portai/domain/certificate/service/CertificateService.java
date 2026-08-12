package com.portai.domain.certificate.service;

import com.portai.domain.certificate.dto.CertificateRequest;
import com.portai.domain.certificate.dto.CertificateResponse;
import com.portai.domain.certificate.entity.Certificate;
import com.portai.domain.certificate.repository.CertificateRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    // 자격증 등록
    @Transactional
    public CertificateResponse createCertificate(Long userId, CertificateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Certificate certificate = Certificate.builder()
                .user(user)
                .name(request.getName())
                .issuer(request.getIssuer())
                .acquiredDate(request.getAcquiredDate())
                .expiryDate(request.getExpiryDate())
                .score(request.getScore())
                .build();

        Certificate saved = certificateRepository.save(certificate);

        return CertificateResponse.from(saved);
    }

    // 특정 사용자의 자격증 목록 조회
    public List<CertificateResponse> getMyCertificates(Long userId) {

        return certificateRepository.findAllByUserId(userId)
                .stream()
                .map(CertificateResponse::from)
                .collect(Collectors.toList());
    }

    // 자격증 수정
    @Transactional
    public CertificateResponse updateCertificate(
            Long userId,
            Long certId,
            CertificateRequest request
    ) {

        Certificate certificate = findCertificateOrThrow(certId);

        validateOwner(certificate, userId);

        certificate.update(
                request.getName(),
                request.getIssuer(),
                request.getAcquiredDate(),
                request.getExpiryDate(),
                request.getScore()
        );

        return CertificateResponse.from(certificate);
    }

    // 자격증 삭제
    @Transactional
    public void deleteCertificate(Long userId, Long certId) {

        Certificate certificate = findCertificateOrThrow(certId);

        validateOwner(certificate, userId);

        certificateRepository.delete(certificate);
    }

    private Certificate findCertificateOrThrow(Long certId) {
        return certificateRepository.findById(certId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.CERTIFICATE_NOT_FOUND));
    }

    private void validateOwner(Certificate certificate, Long userId) {

        if (!certificate.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.CERTIFICATE_ACCESS_DENIED);
        }
    }
}