package com.portai.infra.llmclient;

import org.springframework.stereotype.Component;

/**
 * 실제 LLM 연동 전까지 사용하는 임시 구현체.
 * 항상 자리표시자 텍스트를 리턴함 - 실제 LLM(OpenAI/Claude 등) 붙으면
 * 이 클래스 대신 새 구현체(@Component 하나만) 갈아끼우면 됨.
 */
@Component
public class MockLlmClient implements LlmClient {

    @Override
    public String generateText(String prompt) {
        return "[AI 생성 준비 중입니다. LLM 연동 후 실제 결과로 채워질 예정입니다.]";
    }
}