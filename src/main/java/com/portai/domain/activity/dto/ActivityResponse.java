package com.portai.domain.activity.dto;

import com.portai.domain.activity.entity.Activity;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ActivityResponse {

    private final Long id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String role;
    private final String description;
    private final LocalDateTime createdAt;

    public ActivityResponse(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.startDate = activity.getStartDate();
        this.endDate = activity.getEndDate();
        this.role = activity.getRole();
        this.description = activity.getDescription();
        this.createdAt = activity.getCreatedAt();
    }
}