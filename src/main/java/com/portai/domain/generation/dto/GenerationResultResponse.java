package com.portai.domain.generation.dto;

import com.portai.domain.generation.entity.GenerationResult;
import com.portai.domain.generation.entity.GenerationResultStatus;
import com.portai.domain.generation.entity.GenerationResultType;
import lombok.Getter;

@Getter
public class GenerationResultResponse {

    private final Long id;
    private final GenerationResultType type;
    private final GenerationResultStatus status;
    private final String content;
    private final String fileUrl;
    private final String failReason;
    private final boolean edited;

    public GenerationResultResponse(GenerationResult result) {
        this.id = result.getId();
        this.type = result.getType();
        this.status = result.getStatus();
        this.content = result.getContent();
        this.fileUrl = result.getFileUrl();
        this.failReason = result.getFailReason();
        this.edited = result.isEdited();
    }
}
