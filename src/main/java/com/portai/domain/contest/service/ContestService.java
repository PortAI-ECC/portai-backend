package com.portai.domain.contest.service;

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
                request.getResult()
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
}