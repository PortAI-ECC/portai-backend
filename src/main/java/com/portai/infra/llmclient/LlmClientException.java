package com.portai.infra.llmclient; // TODO: 실제 패키지명으로 변경

/**
 * LLM 호출 실패(네트워크 오류, API 에러, 타임아웃 등) 시 던지는 예외.
 * GenerationService에서 이 예외를 잡아 generation_results.status를 FAILED로,
 * fail_reason에 메시지를 저장하면 됨.
 */
public class LlmClientException extends RuntimeException {

    public LlmClientException(String message) {
        super(message);
    }

    public LlmClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
