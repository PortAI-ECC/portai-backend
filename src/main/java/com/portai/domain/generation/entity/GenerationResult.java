package com.portai.domain.generation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "generation_results",
        uniqueConstraints = @UniqueConstraint(name = "uq_generation_type", columnNames = {"generation_id", "type"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 표준)
public class GenerationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK, Auto Increment
    private Long id;

    // 결과물이 속한 생성 요청 (N:1) - Generation이 애그리거트 루트이므로 직접 저장하지 않고 addResult()로만 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id", nullable = false)
    private Generation generation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GenerationResultType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GenerationResultStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "fail_reason", length = 100)
    private String failReason;

    // 사용자가 직접 수정했는지 여부 (재생성 시 덮어쓰지 않기 위함)
    @Column(nullable = false)
    private boolean edited;

    @Builder
    public GenerationResult(GenerationResultType type) {
        this.type = type;
        this.status = GenerationResultStatus.IN_PROGRESS;
        this.edited = false;
    }

    // Generation.addResult() 에서만 호출되는 양방향 연관관계 편의 메서드
    void assignGeneration(Generation generation) {
        this.generation = generation;
    }

    // 생성 완료 - LLM 결과 반영
    public void complete(String content, String fileUrl) {
        this.status = GenerationResultStatus.COMPLETED;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    // 생성 실패
    public void fail(String reason) {
        this.status = GenerationResultStatus.FAILED;
        this.failReason = reason;
    }

    // 사용자가 결과물을 직접 수정 (재생성 시 덮어쓰지 않도록 edited 플래그 설정)
    public void editContent(String newContent) {
        this.content = newContent;
        this.edited = true;
    }
}
