package com.portai.infra.llmclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 실제 LLM 연동 전까지 / 로컬 개발·테스트용으로 사용하는 임시 구현체.
 * 항상 자리표시자 텍스트를 리턴함.
 *
 * application.yml의 llm.provider 값이 없거나 "mock"이면 이 빈이 등록됨 (기본값).
 * 실제 배포 시 Railway/Render 환경변수에서 LLM_PROVIDER=openai로 설정하면
 * OpenAiClient가 대신 등록됨.
 */
@Component
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockLlmClient implements LlmClient {

    @Override
    public String generateText(String prompt) {
        return "[AI 생성 준비 중입니다. LLM 연동 후 실제 결과로 채워질 예정입니다. (prompt 길이: "
                + prompt.length() + "자)]";
    }
}