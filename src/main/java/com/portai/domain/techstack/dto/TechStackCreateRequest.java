package com.portai.domain.techstack.dto;

import com.portai.domain.techstack.entity.Proficiency;
import com.portai.domain.techstack.entity.TechCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트엔드에서 기술 스택을 추가할 때 보내는 요청(Request) DTO
 */
@Getter
@NoArgsConstructor
public class TechStackCreateRequest {

    private String name;
    private TechCategory category;
    private Proficiency proficiency;

}