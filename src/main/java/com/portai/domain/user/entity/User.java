package com.portai.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(name = "intro_one_liner", length = 200)
    private String introOneLiner;

    // @Enumerated(EnumType.STRING) 삭제 및 String으로 변경
    @Column(name = "desired_job")
    private String desiredJob;

    @Column(name = "desired_company", length = 100)
    private String desiredCompany;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // 회원가입 시 처음에 데이터를 집어넣기 위한 Builder
    @Builder
    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String phone, String introOneLiner, String desiredJob, String desiredCompany, String profileImageUrl) {
        if (phone != null) {
            this.phone = phone;
        }
        if (introOneLiner != null) {
            this.introOneLiner = introOneLiner;
        }
        if (desiredJob != null) {
            this.desiredJob = desiredJob;
        }
        if (desiredCompany != null) {
            this.desiredCompany = desiredCompany;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}