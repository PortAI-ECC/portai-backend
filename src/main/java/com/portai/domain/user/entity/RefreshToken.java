package com.portai.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 토큰인지 식별하기 위한 이메일 (한 명당 하나의 토큰만 가져야 하므로 unique = true 설정)
    @Column(nullable = false, unique = true)
    private String userEmail;

    // 실제 발급된 리프레시 토큰 문자열을 저장
    @Column(nullable = false)
    private String token;

    // 처음 생성할 때 사용할 생성자
    public RefreshToken(String userEmail, String token) {
        this.userEmail = userEmail;
        this.token = token;
    }

    // 나중에 토큰을 새것으로 교체할 때 사용할 메서드
    public void updateToken(String token) {
        this.token = token;
    }
}