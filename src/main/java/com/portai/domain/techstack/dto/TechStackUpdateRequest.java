package com.portai.domain.techstack.dto;

import com.portai.domain.techstack.entity.Proficiency;
import com.portai.domain.techstack.entity.TechCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TechStackUpdateRequest {
    private TechCategory category;
    private Proficiency proficiency;
    private String freeText;
}