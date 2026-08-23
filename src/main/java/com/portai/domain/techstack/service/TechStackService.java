package com.portai.domain.techstack.service;

import com.portai.infra.llmclient.LlmClient;
import com.portai.domain.techstack.dto.TechStackCreateRequest;
import com.portai.domain.techstack.dto.TechStackReorderRequest;
import com.portai.domain.techstack.dto.TechStackResponse;
import com.portai.domain.techstack.dto.TechStackUpdateRequest;
import com.portai.domain.techstack.entity.TechStack;
import com.portai.domain.techstack.repository.TechStackRepository;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 기술 스택과 관련된 핵심 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechStackService {

    private final TechStackRepository techStackRepository;
    private final UserRepository userRepository;
    private final LlmClient llmClient;

    /**
     * 1. 기술 스택 목록 조회 (GET)
     */
    public List<TechStackResponse> getTechStacks(Long userId) {

        List<TechStack> techStacks = techStackRepository.findByUserIdOrderByOrderIndexAsc(userId);

        return techStacks.stream()
                .map(TechStackResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. 기술 스택 개별 추가 (POST)
     */
    @Transactional
    public Long addTechStack(Long userId, TechStackCreateRequest request) {
        // 중복 기술 검사 -> 409 CONFLICT 커스텀 에러 던짐
        if (techStackRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_TECH_STACK);
        }

        // 유저 정보 조회 -> 404 NOT FOUND 커스텀 에러 던짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 유저의 가장 마지막 순서를 찾아서 +1 해줌
        Integer nextOrderIndex = techStackRepository.findMaxOrderIndexByUserId(userId) + 1;

        TechStack techStack = TechStack.builder()
                .user(user)
                .name(request.getName())
                .category(request.getCategory())
                .proficiency(request.getProficiency())
                .orderIndex(nextOrderIndex)
                .freeText(request.getFreeText())
                .build();

        return techStackRepository.save(techStack).getId();
    }

    /**
     * 3. 기술 스택 개별 삭제 (DELETE)
     */
    @Transactional
    public void deleteTechStack(Long userId, Long skillId) {
        // 본인의 기술 스택이 맞는지 검증하며 조회 -> 없거나 내 것이 아니면 404 NOT FOUND 에러 던짐
        TechStack techStack = techStackRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TECH_STACK_NOT_FOUND));

        techStackRepository.delete(techStack);
    }

    /**
     * 4. 기술 스택 개별 수정 (PATCH)
     */
    @Transactional
    public void updateTechStack(Long userId, Long skillId, TechStackUpdateRequest request) {
        // 본인 소유 검증
        TechStack techStack = techStackRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TECH_STACK_NOT_FOUND));

        // 엔티티 내부 메서드를 통해 안전하게 값 변경 (JPA Dirty Checking으로 자동 업데이트됨)
        techStack.updateTechStack(request.getCategory(), request.getProficiency(), request.getFreeText());
    }

    /**
     * 5. 기술 스택 순서 재정렬 (PUT)
     */
    @Transactional
    public void reorderTechStacks(Long userId, TechStackReorderRequest request) {
        List<Long> skillIds = request.getSkillIds();

        for (int i = 0; i < skillIds.size(); i++) {
            Long skillId = skillIds.get(i);

            // 본인 소유 검증 후 순서(index) 덮어씌우기
            TechStack techStack = techStackRepository.findByIdAndUserId(skillId, userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TECH_STACK_NOT_FOUND));

            techStack.updateOrderIndex(i + 1);
        }
    }

    /**
     * 6. 기술 스택 AI 초안 생성 (LLM 연동)
     */
    public String generateTechStackDescription(Long userId, Long skillId) {
        // 1. 기존 방식대로 본인 소유 검증
        TechStack techStack = techStackRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TECH_STACK_NOT_FOUND));

        // 2. 기술 스택 데이터로 프롬프트 조립 (Enum인 숙련도는 .name()으로 문자로 변환)
        String prompt = String.format(
                "너는 전문 개발자 이력서 컨설턴트야. 다음 기술 스택 데이터를 바탕으로 포트폴리오에 들어갈 3~4줄짜리 활용 경험 및 역량 요약 초안을 작성해줘.\n" +
                        "- 기술명: %s\n" +
                        "- 숙련도: %s\n" +
                        "- 작성한 메모(자유텍스트): %s",
                techStack.getName(),
                techStack.getProficiency() != null ? techStack.getProficiency().name() : "없음",
                techStack.getFreeText() != null ? techStack.getFreeText() : "없음"
        );

        // 3. LLM 호출
        return llmClient.generateText(prompt);
    }
}