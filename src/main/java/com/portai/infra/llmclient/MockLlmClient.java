package com.portai.infra.llmclient; // TODO: 실제 패키지명으로 변경

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 로컬 개발/테스트용 Mock 구현체.
 * 실제 OpenAI API를 호출하지 않고 즉시 가짜 텍스트를 반환한다.
 * 비용 없이 generations 플로우(엔티티 저장, 상태 전이, 컨트롤러 응답 등)를 통째로 확인할 때 사용.
 *
 * application.yml의 llm.provider 값이 없거나 "mock"이면 이 빈이 등록됨 (기본값).
 * 실제 배포 시 Railway/Render 환경변수에서 LLM_PROVIDER=openai로 설정하면
 * OpenAiClient가 대신 등록됨.
 */
@Component
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockLlmClient implements LlmClient {

    @Override
    public String generate(String systemPrompt, String userPrompt) {
        return """
                [MOCK 응답 - 실제 LLM 호출 아님]

                이 텍스트는 llm.provider=mock 상태에서 생성된 더미 결과입니다.
                실제 배포 환경에서는 OPENAI_API_KEY와 llm.provider=openai 설정 후
                이 자리에 실제 생성된 STAR 형식 이력서 내용이 들어갑니다.

                (전달받은 프롬프트 길이 - system: %d자, user: %d자)
                """.formatted(systemPrompt.length(), userPrompt.length());
    }
}
