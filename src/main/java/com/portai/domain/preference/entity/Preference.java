package com.portai.domain.preference.entity;

import com.portai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와 1:1 매핑 (한 유저는 하나의 설정만 가짐)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // DB의 JSON 타입을 Java의 List<String>으로 자동 변환
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> keywords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> emphasizedTypes;

    // Enum 타입 매핑 (DB에는 문자열로 저장)
    @Enumerated(EnumType.STRING)
    private Style style;

    @Builder
    public Preference(User user, List<String> keywords, List<String> emphasizedTypes, Style style) {
        this.user = user;
        this.keywords = keywords;
        this.emphasizedTypes = emphasizedTypes;
        this.style = style;
    }

    // 부분 수정(PATCH)을 위한 더티 체킹 메서드
    public void updatePreference(List<String> keywords, List<String> emphasizedTypes, Style style) {
        if (keywords != null) this.keywords = keywords;
        if (emphasizedTypes != null) this.emphasizedTypes = emphasizedTypes;
        if (style != null) this.style = style;
    }
}