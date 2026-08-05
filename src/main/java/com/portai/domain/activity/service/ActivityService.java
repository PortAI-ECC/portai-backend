package com.portai.domain.activity.service;

import com.portai.domain.activity.dto.ActivityRequest;
import com.portai.domain.activity.dto.ActivityResponse;
import com.portai.domain.activity.entity.Activity;
import com.portai.domain.activity.repository.ActivityRepository;
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
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivityResponse createActivity(Long userId, ActivityRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Activity activity = Activity.builder()
                .user(user)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .role(request.getRole())
                .description(request.getDescription())
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
                request.getDescription()
        );

        return new ActivityResponse(activity);
    }

    @Transactional
    public void deleteActivity(Long userId, Long activityId) {

        Activity activity = findActivityOrThrow(activityId);

        validateOwner(activity, userId);

        activityRepository.delete(activity);
    }

    private Activity findActivityOrThrow(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACTIVITY_NOT_FOUND));
    }

    private void validateOwner(Activity activity, Long userId) {

        if (!activity.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACTIVITY_ACCESS_DENIED);
        }
    }
}