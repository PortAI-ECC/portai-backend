package com.portai.domain.generation.service;

import com.portai.domain.generation.dto.GenerationRequest;
import com.portai.domain.generation.dto.GenerationResponse;
import com.portai.domain.generation.dto.GenerationResultResponse;
import com.portai.domain.generation.dto.GenerationResultUpdateRequest;
import com.portai.domain.generation.entity.Generation;
import com.portai.domain.generation.entity.GenerationResult;
import com.portai.domain.generation.entity.GenerationResultType;
import com.portai.domain.generation.repository.GenerationRepository;
import com.portai.domain.jobposting.entity.JobPosting;
import com.portai.domain.jobposting.repository.JobPostingRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerationService {

    private final GenerationRepository generationRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;

    /**
     * 결과물 생성 요청 등록.
     * 요청받은 유형(RESUME, PORTFOLIO 등)마다 GenerationResult를 하나씩 만들어두고,
     * 실제 LLM 호출은 infra/llmclient 연동 후 비동기로 채워지도록 status만 IN_PROGRESS로 시작한다.
     */
    @Transactional
    public GenerationResponse createGeneration(Long userId, GenerationRequest request) {
        User user = getUserOrThrow(userId);
        JobPosting jobPosting = getJobPostingOrNull(userId, request.getJobPostingId());

        Generation generation = buildGeneration(user, jobPosting, request.getStyle(), request.getTemplateId(), request.getTypes());
        Generation saved = generationRepository.save(generation);
        return new GenerationResponse(saved);
    }

    /**
     * 동일 조건 재생성 - 기존 생성 요청의 jobPosting/style/결과물 유형을 그대로 이어받아
     * 새 생성 이력을 하나 더 만든다 (기존 이력은 그대로 보존).
     */
    @Transactional
    public GenerationResponse regenerate(Long userId, Long generationId) {
        Generation source = findOwnedGenerationOrThrow(userId, generationId);

        List<GenerationResultType> types = source.getResults().stream()
                .map(GenerationResult::getType)
                .collect(Collectors.toList());

        Generation regenerated = buildGeneration(source.getUser(), source.getJobPosting(), source.getStyle(), source.getTemplateId(), types);
        Generation saved = generationRepository.save(regenerated);
        return new GenerationResponse(saved);
    }

    /**
     * 내 생성 이력 목록 조회 (최신순)
     */
    @Transactional(readOnly = true)
    public List<GenerationResponse> getMyGenerations(Long userId) {
        return generationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(GenerationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 생성 결과 상세 조회 (결과물 목록 포함, 본인 소유만 가능)
     */
    @Transactional(readOnly = true)
    public GenerationResponse getGeneration(Long userId, Long generationId) {
        return new GenerationResponse(findOwnedGenerationOrThrow(userId, generationId));
    }

    /**
     * 생성 결과물 중 특정 유형을 사용자가 직접 수정 (본인 소유만 가능)
     */
    @Transactional
    public GenerationResponse editResult(Long userId, Long generationId, GenerationResultType type,
                                          GenerationResultUpdateRequest request) {
        Generation generation = findOwnedGenerationOrThrow(userId, generationId);
        GenerationResult result = findResultOrThrow(generation, type);
        result.editContent(request.getContent());
        return new GenerationResponse(generation);
    }

    /**
     * 결과물 파일 다운로드용 데이터 조회 (본인 소유만 가능).
     * 실제 파일(fileUrl)이 아직 없는 상태(자리표시자)라면 컨트롤러에서 content를 텍스트 파일로 변환해 내려준다.
     */
    @Transactional(readOnly = true)
    public GenerationResultResponse getResultForDownload(Long userId, Long generationId, GenerationResultType type) {
        Generation generation = findOwnedGenerationOrThrow(userId, generationId);
        return new GenerationResultResponse(findResultOrThrow(generation, type));
    }

    /**
     * 생성 이력 삭제 (결과물도 cascade로 함께 삭제, 본인 소유만 가능)
     */
    @Transactional
    public void deleteGeneration(Long userId, Long generationId) {
        generationRepository.delete(findOwnedGenerationOrThrow(userId, generationId));
    }

    private Generation buildGeneration(User user, JobPosting jobPosting, String style, String templateId, List<GenerationResultType> types) {
        Generation generation = Generation.builder()
                .user(user)
                .jobPosting(jobPosting)
                .style(style)
                .templateId(templateId)
                .build();

        // 같은 요청 안에서 유형이 중복되더라도 한 번씩만 생성 (uq_generation_type 제약 위반 방지)
        Set<GenerationResultType> distinctTypes = new LinkedHashSet<>(types);
        for (GenerationResultType type : distinctTypes) {
            generation.addResult(GenerationResult.builder().type(type).build());
        }
        return generation;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    private JobPosting getJobPostingOrNull(Long userId, Long jobPostingId) {
        if (jobPostingId == null) {
            return null;
        }
        return jobPostingRepository.findByIdAndUserId(jobPostingId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POSTING_NOT_FOUND));
    }

    private Generation findOwnedGenerationOrThrow(Long userId, Long generationId) {
        return generationRepository.findByIdAndUserId(generationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.GENERATION_NOT_FOUND));
    }

    private GenerationResult findResultOrThrow(Generation generation, GenerationResultType type) {
        return generation.getResults().stream()
                .filter(r -> r.getType() == type)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.GENERATION_RESULT_NOT_FOUND));
    }
}
