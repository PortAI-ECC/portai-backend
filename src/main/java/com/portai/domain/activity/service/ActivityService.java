package com.portai.domain.activity.service;

import com.portai.domain.activity.dto.ActivityRequest;
import com.portai.domain.activity.dto.ActivityResponse;
import com.portai.domain.activity.entity.Activity;
import com.portai.domain.activity.repository.ActivityRepository;
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
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final LlmClient llmClient;

    @Transactional
    public ActivityResponse createActivity(
            Long userId,
            ActivityRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Activity activity = Activity.builder()
                .user(user)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .role(request.getRole())
                .description(request.getDescription())
                .freeText(request.getFreeText())
                .build();

        Activity saved = activityRepository.save(activity);

        return new ActivityResponse(saved);
    }

    public ActivityResponse getActivity(Long activityId) {

        Activity activity = findActivityOrThrow(activityId);

        return new ActivityResponse(activity);
    }

    public List<ActivityResponse> getMyActivities(Long userId) {

        return activityRepository.findAllByUserId(userId)
                .stream()
                .map(ActivityResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityResponse updateActivity(
            Long userId,
            Long activityId,
            ActivityRequest request
    ) {

        Activity activity = findActivityOrThrow(activityId);

        validateOwner(activity, userId);

        activity.update(
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRole(),
                request.getDescription(),
                request.getFreeText()
        );

        return new ActivityResponse(activity);
    }

    @Transactional
    public void deleteActivity(Long userId, Long activityId) {

        Activity activity = findActivityOrThrow(activityId);

        validateOwner(activity, userId);

        activityRepository.delete(activity);
    }

    /**
     * 활동이력 AI 초안 생성
     */
    @Transactional(readOnly = true)
    public String generateActivityDescription(
            Long userId,
            Long activityId
    ) {

        Activity activity = findActivityOrThrow(activityId);

        validateOwner(activity, userId);

        String prompt = String.format(
                "너는 전문 이력서 컨설턴트야. 다음 활동이력 데이터를 바탕으로 포트폴리오에 들어갈 3~4줄짜리 성과 및 역량 중심 요약 초안을 작성해줘. 제공되지 않은 사실은 임의로 만들지 마.\n" +
                        "- 활동명: %s\n" +
                        "- 시작일: %s\n" +
                        "- 종료일: %s\n" +
                        "- 역할: %s\n" +
                        "- 활동 설명: %s\n" +
                        "- 작성한 메모(자유텍스트): %s",
                activity.getName(),
                activity.getStartDate() != null
                        ? activity.getStartDate().toString()
                        : "없음",
                activity.getEndDate() != null
                        ? activity.getEndDate().toString()
                        : "없음",
                activity.getRole() != null
                        ? activity.getRole()
                        : "없음",
                activity.getDescription() != null
                        ? activity.getDescription()
                        : "없음",
                activity.getFreeText() != null
                        ? activity.getFreeText()
                        : "없음"
        );

        return llmClient.generateText(prompt);
    }

    private Activity findActivityOrThrow(Long activityId) {

        return activityRepository.findById(activityId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.ACTIVITY_NOT_FOUND));
    }

    private void validateOwner(Activity activity, Long userId) {

        if (!activity.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACTIVITY_ACCESS_DENIED);
        }
    }
}