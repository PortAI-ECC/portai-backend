package com.portai.domain.techstack.service;

import com.portai.domain.techstack.dto.TechStackCreateRequest;
import com.portai.domain.techstack.dto.TechStackResponse;
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

    /**
     * 1. 기술 스택 목록 조회 (GET)
     */
    public List<TechStackResponse> getTechStacks(Long userId) {
        List<TechStack> techStacks = techStackRepository.findByUserId(userId);

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

        TechStack techStack = TechStack.builder()
                .user(user)
                .name(request.getName())
                .category(request.getCategory())
                .proficiency(request.getProficiency())
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
}