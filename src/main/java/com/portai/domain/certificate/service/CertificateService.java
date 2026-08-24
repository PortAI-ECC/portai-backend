package com.portai.domain.certificate.service;

import com.portai.domain.certificate.dto.CertificateRequest;
import com.portai.domain.certificate.dto.CertificateResponse;
import com.portai.domain.certificate.entity.Certificate;
import com.portai.domain.certificate.repository.CertificateRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.infra.llmclient.LlmClient;
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
    private final LlmClient llmClient;

    // 자격증 등록
    @Transactional
    public CertificateResponse createCertificate(
            Long userId,
            CertificateRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Certificate certificate = Certificate.builder()
                .user(user)
                .name(request.getName())
                .issuer(request.getIssuer())
                .acquiredDate(request.getAcquiredDate())
                .expiryDate(request.getExpiryDate())
                .score(request.getScore())
                .freeText(request.getFreeText())
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
                request.getScore(),
                request.getFreeText()
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

    /**
     * 자격증 AI 초안 생성
     */
    @Transactional(readOnly = true)
    public String generateCertificateDescription(Long userId, Long certId) {

        Certificate certificate = findCertificateOrThrow(certId);

        validateOwner(certificate, userId);

        String prompt = String.format(
                "너는 전문 이력서 컨설턴트야. 다음 자격증 데이터를 바탕으로 포트폴리오에 들어갈 3~4줄짜리 역량 중심 요약 초안을 작성해줘. 제공되지 않은 사실은 임의로 만들지 마.\n" +
                        "- 자격증명: %s\n" +
                        "- 발급기관: %s\n" +
                        "- 취득일: %s\n" +
                        "- 만료일: %s\n" +
                        "- 점수 또는 등급: %s\n" +
                        "- 작성한 메모(자유텍스트): %s",
                certificate.getName(),
                certificate.getIssuer() != null
                        ? certificate.getIssuer()
                        : "없음",
                certificate.getAcquiredDate() != null
                        ? certificate.getAcquiredDate().toString()
                        : "없음",
                certificate.getExpiryDate() != null
                        ? certificate.getExpiryDate().toString()
                        : "없음",
                certificate.getScore() != null
                        ? certificate.getScore()
                        : "없음",
                certificate.getFreeText() != null
                        ? certificate.getFreeText()
                        : "없음"
        );

        return llmClient.generateText(prompt);
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