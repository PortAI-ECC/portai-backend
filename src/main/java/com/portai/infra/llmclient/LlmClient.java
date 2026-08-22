package com.portai.infra.llmclient; // TODO: 실제 패키지명으로 변경

/**
 * LLM 제공자(OpenAI, Claude 등)를 추상화하는 인터페이스.
 * 나중에 다른 LLM으로 바꾸거나 provider를 여러 개 둬야 할 때
 * 이 인터페이스만 구현체를 갈아끼우면 됨.
 */
public interface LlmClient {

    /**
     * 주어진 프롬프트로 LLM을 호출하고 텍스트 응답을 반환한다.
     *
     * @param systemPrompt 시스템 지시문 (문체, 역할 등)
     * @param userPrompt   실제 생성 요청 내용 (사용자 데이터 기반으로 구성된 프롬프트)
     * @return LLM이 생성한 텍스트
     */
    String generate(String systemPrompt, String userPrompt);
}
