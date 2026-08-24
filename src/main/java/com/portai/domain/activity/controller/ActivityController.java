package com.portai.domain.activity.controller;

import com.portai.domain.activity.dto.ActivityRequest;
import com.portai.domain.activity.dto.ActivityResponse;
import com.portai.domain.activity.service.ActivityService;
import com.portai.global.annotation.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ActivityResponse createActivity(
            @RequestParam Long userId,
            @Valid @RequestBody ActivityRequest request
    ) {
        return activityService.createActivity(userId, request);
    }

    @GetMapping("/{activityId}")
    public ActivityResponse getActivity(
            @PathVariable Long activityId
    ) {
        return activityService.getActivity(activityId);
    }

    @GetMapping("/me")
    public List<ActivityResponse> getMyActivities(
            @RequestParam Long userId
    ) {
        return activityService.getMyActivities(userId);
    }

    @PutMapping("/{activityId}")
    public ActivityResponse updateActivity(
            @RequestParam Long userId,
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityRequest request
    ) {
        return activityService.updateActivity(
                userId,
                activityId,
                request
        );
    }

    @DeleteMapping("/{activityId}")
    public void deleteActivity(
            @RequestParam Long userId,
            @PathVariable Long activityId
    ) {
        activityService.deleteActivity(
                userId,
                activityId
        );
    }

    /**
     * 활동이력 AI 초안 생성
     * POST /activities/{activityId}/description/generate
     */
    @PostMapping("/{activityId}/description/generate")
    public ResponseEntity<Map<String, String>> generateDescription(
            @AuthUser Long userId,
            @PathVariable Long activityId
    ) {

        String generatedText =
                activityService.generateActivityDescription(
                        userId,
                        activityId
                );

        return ResponseEntity.ok(Map.of(
                "generatedDescription", generatedText
        ));
    }
}