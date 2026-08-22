package com.portai.domain.activity.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 100)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "free_text", columnDefinition = "TEXT")
    private String freeText;

    @Builder
    public Activity(
            User user,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            String role,
            String description,
            String freeText
    ) {
        this.user = user;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.role = role;
        this.description = description;
        this.freeText = freeText;
    }

    public void update(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            String role,
            String description,
            String freeText
    ) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.role = role;
        this.description = description;
        this.freeText = freeText;
    }
}