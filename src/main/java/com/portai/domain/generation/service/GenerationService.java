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
import com.portai.infra.llmclient.LlmClient;
import com.portai.infra.llmclient.LlmClientException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerationService {

    private static final Logger log = LoggerFactory.getLogger(GenerationService.class);

    private final GenerationRepository generationRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;
    private final UserContextAggregator userContextAggregator;

    @Transactional
    public GenerationResponse createGeneration(Long userId, GenerationRequest request) {
        User user = getUserOrThrow(userId);
        JobPosting jobPosting = getJobPostingOrNull(userId, request.getJobPostingId());

        Generation generation = buildGeneration(user, jobPosting, request.getStyle(), request.getTypes());
        Generation saved = generationRepository.save(generation);

        processResults(saved);

        return new GenerationResponse(saved);
    }

    @Transactional
    public GenerationResponse regenerate(Long userId, Long generationId) {
        Generation source = findOwnedGenerationOrThrow(userId, generationId);

        List<GenerationResultType> types = source.getResults().stream()
                .map(GenerationResult::getType)
                .collect(Collectors.toList());

        Generation regenerated = buildGeneration(source.getUser(), source.getJobPosting(), source.getStyle(), types);
        Generation saved = generationRepository.save(regenerated);

        processResults(saved);

        return new GenerationResponse(saved);
    }

    public List<GenerationResponse> getMyGenerations(Long userId) {
        return generationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(GenerationResponse::new)
                .collect(Collectors.toList());
    }

    public GenerationResponse getGeneration(Long userId, Long generationId) {
        return new GenerationResponse(findOwnedGenerationOrThrow(userId, generationId));
    }

    @Transactional
    public GenerationResponse editResult(Long userId, Long generationId, GenerationResultType type,
                                          GenerationResultUpdateRequest request) {
        Generation generation = findOwnedGenerationOrThrow(userId, generationId);
        GenerationResult result = findResultOrThrow(generation, type);
        result.editContent(request.getContent());
        return new GenerationResponse(generation);
    }

    public GenerationResultResponse getResultForDownload(Long userId, Long generationId, GenerationResultType type) {
        Generation generation = findOwnedGenerationOrThrow(userId, generationId);
        return new GenerationResultResponse(findResultOrThrow(generation, type));
    }

    @Transactional
    public void deleteGeneration(Long userId, Long generationId) {
        generationRepository.delete(findOwnedGenerationOrThrow(userId, generationId));
    }

    private void processResults(Generation generation) {
        String userContext = userContextAggregator.buildUserContext(generation.getUser().getId());
        String jobPostingText = null;

        for (GenerationResult result : generation.getResults()) {
            try {
                String systemPrompt = promptBuilder.buildSystemPrompt(generation.getStyle(), result.getType());
                String userPrompt = promptBuilder.buildUserPrompt(userContext, jobPostingText);

                String content = llmClient.generate(systemPrompt, userPrompt);
                result.complete(content, null);

            } catch (LlmClientException e) {
                log.error("generation_result 생성 실패. generationId={}, type={}", generation.getId(), result.getType(), e);
                result.fail(truncate(e.getMessage()));
            } catch (Exception e) {
                log.error("예상치 못한 오류로 generation_result 생성 실패. generationId={}, type={}",
                        generation.getId(), result.getType(), e);
                result.fail(truncate("알 수 없는 오류가 발생했습니다."));
            }
        }

        generation.refreshOverallStatus();
    }

    private String truncate(String reason) {
        if (reason == null) {
            return null;
        }
        return reason.length() > 100 ? reason.substring(0, 100) : reason;
    }

    private Generation buildGeneration(User user, JobPosting jobPosting, String style, List<GenerationResultType> types) {
        Generation generation = Generation.builder()
                .user(user)
                .jobPosting(jobPosting)
                .style(style)
                .build();

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