package com.portai.domain.techstack.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TechStackReorderRequest {
    // 프론트에서 바뀐 순서대로 skillId 배열을 보내줍니다. (예: [3, 1, 5, 2])
    private List<Long> skillIds;
}