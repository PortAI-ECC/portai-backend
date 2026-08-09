package com.portai.domain.techstack.dto;

import com.portai.domain.techstack.entity.Proficiency;
import com.portai.domain.techstack.entity.TechCategory;
import com.portai.domain.techstack.entity.TechSource;
import com.portai.domain.techstack.entity.TechStack;
import lombok.Builder;
import lombok.Getter;

/**
 * 프론트엔드에 기술 스택 정보를 돌려줄 때 사용하는 응답(Response) DTO
 */
@Getter
@Builder
public class TechStackResponse {

    private Long skillId;
    private String name;
    private TechCategory category;
    private Proficiency proficiency;
    private TechSource source;

    // Entity를 DTO로 쉽게 변환하기 위한 편의 메서드
    public static TechStackResponse from(TechStack techStack) {
        return TechStackResponse.builder()
                .skillId(techStack.getId())
                .name(techStack.getName())
                .category(techStack.getCategory())
                .proficiency(techStack.getProficiency())
                .source(techStack.getSource())
                .build();
    }
}