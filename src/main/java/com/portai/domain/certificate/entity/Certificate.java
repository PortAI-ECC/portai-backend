package com.portai.domain.certificate.entity;

import com.portai.domain.user.entity.User;
import com.portai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String issuer;

    @Column(name = "acquired_date")
    private LocalDate acquiredDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(length = 50)
    private String score;

    @Builder
    public Certificate(
            User user,
            String name,
            String issuer,
            LocalDate acquiredDate,
            LocalDate expiryDate,
            String score
    ) {
        this.user = user;
        this.name = name;
        this.issuer = issuer;
        this.acquiredDate = acquiredDate;
        this.expiryDate = expiryDate;
        this.score = score;
    }

    public void update(
            String name,
            String issuer,
            LocalDate acquiredDate,
            LocalDate expiryDate,
            String score
    ) {
        this.name = name;
        this.issuer = issuer;
        this.acquiredDate = acquiredDate;
        this.expiryDate = expiryDate;
        this.score = score;
    }
}