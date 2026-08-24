package com.portai.domain.contest.service;

import com.portai.infra.llmclient.LlmClient;
import com.portai.domain.contest.dto.ContestCreateRequest;
import com.portai.domain.contest.dto.ContestResponse;
import com.portai.domain.contest.dto.ContestUpdateRequest;
import com.portai.domain.contest.entity.Contest;
import com.portai.domain.contest.repository.ContestRepository;
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
@Transactional(readOnly = true)
public class ContestService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final LlmClient llmClient;
    /**
     * 1. 공모전 목록 조회 (GET)
     */
    public List<ContestResponse> getContests(Long userId) {
        List<Contest> contests = contestRepository.findByUserIdOrderByIdDesc(userId);

        return contests.stream()
                .map(ContestResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. 공모전 등록 (POST)
     */
    @Transactional
    public Long addContest(Long userId, ContestCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Contest contest = Contest.builder()
                .user(user)
                .name(request.getName())
                .host(request.getHost())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .awarded(request.getAwarded())
                .role(request.getRole())
                .result(request.getResult())
                .freeText(request.getFreeText())
                .build();

        return contestRepository.save(contest).getId();
    }

    /**
     * 3. 공모전 수정 (PATCH)
     */
    @Transactional
    public void updateContest(Long userId, Long contestId, ContestUpdateRequest request) {
        Contest contest = contestRepository.findByIdAndUserId(contestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_NOT_FOUND));

        contest.updateContest(
                request.getName(),
                request.getHost(),
                request.getStartDate(),
                request.getEndDate(),
                request.getAwarded(),
                request.getRole(),
                request.getResult(),
                request.getFreeText()
        );
    }

    /**
     * 4. 공모전 삭제 (DELETE)
     */
    @Transactional
    public void deleteContest(Long userId, Long contestId) {
        Contest contest = contestRepository.findByIdAndUserId(contestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_NOT_FOUND));

        contestRepository.delete(contest);
    }

    /**
     * 5. 공모전 AI 초안 생성 (LLM 연동)
     */
    public String generateContestDescription(Long userId, Long contestId) {
        // 1. 기존 코드와 동일하게 권한 및 존재 여부 확인
        Contest contest = contestRepository.findByIdAndUserId(contestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_NOT_FOUND));

        // 2. 공모전 데이터로 프롬프트 조립
        String prompt = String.format(
                "너는 전문 이력서 컨설턴트야. 다음 공모전 데이터를 바탕으로 포트폴리오에 들어갈 3~4줄짜리 성과 중심 요약 초안을 작성해줘.\n" +
                        "- 공모전명: %s\n" +
                        "- 주최: %s\n" +
                        "- 역할: %s\n" +
                        "- 결과: %s\n" +
                        "- 작성한 메모(자유텍스트): %s",
                contest.getName(),
                contest.getHost() != null ? contest.getHost() : "없음",
                contest.getRole() != null ? contest.getRole() : "없음",
                contest.getResult() != null ? contest.getResult() : "없음",
                contest.getFreeText() != null ? contest.getFreeText() : "없음"
        );

        // 3. LLM 호출
        return llmClient.generateText(prompt);
    }
}