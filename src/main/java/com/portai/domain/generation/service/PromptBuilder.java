package com.portai.domain.generation.service;

import com.portai.domain.generation.entity.GenerationResultType;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildPrompt(String style, GenerationResultType type, String userContext, String jobPostingText) {
        String systemPrompt = buildSystemPrompt(style, type);
        String userPrompt = buildUserPrompt(userContext, jobPostingText);
        return systemPrompt + "\n\n" + userPrompt;
    }

    private String buildSystemPrompt(String style, GenerationResultType type) {
        String styleInstruction = switch (style == null ? "CONCISE" : style) {
            case "JUNIOR_DEVELOPER" -> "신입 개발자다운 배우려는 태도와 성장 가능성을 강조하는 문체로 작성하라.";
            case "DATA_ANALYST" -> "데이터 기반 의사결정과 정량적 성과를 강조하는 문체로 작성하라.";
            case "RESEARCHER" -> "논리적 문제 정의와 검증 과정을 강조하는 문체로 작성하라.";
            case "STARTUP_STYLE" -> "빠른 실행력과 주도성을 강조하는 캐주얼한 문체로 작성하라.";
            case "ENTERPRISE" -> "격식있고 신뢰감 있는 문체로 작성하라.";
            default -> "군더더기 없이 간결한 문체로 작성하라.";
        };

        String typeInstruction = switch (type) {
            case RESUME -> "STAR 형식(Situation, Task, Action, Result)으로 각 경험을 재구성한 이력서 본문을 작성하라.";
            case PORTFOLIO -> "프로젝트 중심으로 기술적 의사결정과 문제 해결 과정을 강조한 포트폴리오 소개글을 작성하라.";
            case SELF_INTRODUCTION -> "성장 과정, 지원 동기, 강점을 자연스럽게 연결한 자기소개서를 작성하라.";
            case INTERVIEW_QUESTIONS -> "제공된 경험을 바탕으로 나올 법한 면접 예상 질문 5개와 답변 방향을 작성하라.";
            case PROJECT_INTRO -> "하나의 프로젝트를 짧고 임팩트 있게 소개하는 한 문단을 작성하라.";
        };

        return """
                너는 이공계 취업준비생의 %s 작성을 돕는 어시스턴트다.
                아래 규칙을 반드시 지켜라.
                1. %s
                2. 사용자가 제공한 정보 안에서만 작성하고, 사실을 지어내지 않는다.
                3. %s
                4. 채용공고 정보가 주어지면 해당 공고의 요구 역량과 자연스럽게 연결되는 표현을 우선한다.
                5. 결과는 마크다운 없이 순수 텍스트로 작성한다.
                """.formatted(type.name(), typeInstruction, styleInstruction);
    }

    private String buildUserPrompt(String userContext, String jobPostingText) {
        StringBuilder sb = new StringBuilder();
        sb.append("[사용자 경험 데이터]\n").append(userContext).append("\n\n");

        if (jobPostingText != null && !jobPostingText.isBlank()) {
            sb.append("[타겟 채용공고]\n").append(jobPostingText).append("\n\n");
        }

        sb.append("위 정보를 바탕으로 작성해줘.");
        return sb.toString();
    }
}