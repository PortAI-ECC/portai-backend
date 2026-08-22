package com.portai.domain.education.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "education")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String school;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ASSOCIATE','BACHELOR','MASTER','DOCTORATE')")
    private Degree degree;

    @Column(length = 100)
    private String major;

    @Column(name = "double_major", length = 100)
    private String doubleMajor;

    @Column(name = "gpa_score", precision = 3, scale = 2)
    private BigDecimal gpaScore;

    @Column(name = "gpa_scale", precision = 3, scale = 2)
    private BigDecimal gpaScale;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ENROLLED','ON_LEAVE','GRADUATED','EXPECTED_GRADUATION')")
    private EducationStatus status;

    @Column(name = "expected_graduation")
    private LocalDate expectedGraduation;

    @Column(name = "free_text", columnDefinition = "TEXT")
    private String freeText;

    @Builder
    public Education(
            User user,
            String school,
            Degree degree,
            String major,
            String doubleMajor,
            BigDecimal gpaScore,
            BigDecimal gpaScale,
            EducationStatus status,
            LocalDate expectedGraduation,
            String freeText
    ) {
        this.user = user;
        this.school = school;
        this.degree = degree;
        this.major = major;
        this.doubleMajor = doubleMajor;
        this.gpaScore = gpaScore;
        this.gpaScale = gpaScale;
        this.status = status;
        this.expectedGraduation = expectedGraduation;
        this.freeText = freeText;
    }

    public void update(
            String school,
            Degree degree,
            String major,
            String doubleMajor,
            BigDecimal gpaScore,
            BigDecimal gpaScale,
            EducationStatus status,
            LocalDate expectedGraduation,
            String freeText
    ) {
        this.school = school;
        this.degree = degree;
        this.major = major;
        this.doubleMajor = doubleMajor;
        this.gpaScore = gpaScore;
        this.gpaScale = gpaScale;
        this.status = status;
        this.expectedGraduation = expectedGraduation;
        this.freeText = freeText;
    }
}