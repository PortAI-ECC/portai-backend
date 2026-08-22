package com.portai.infra.llmclient;

/**
 * LLM 호출 공통 인터페이스.
 * 각 도메인(generations, job-postings, projects, 그리고 공모전/인턴경력/기술스택/자격증/교육/활동이력)에서
 * 이 인터페이스만 주입받아서 사용하면 됨.
 * 실제 LLM(OpenAI 등) 연동은 OpenAiClient가 담당하고, 로컬/테스트에서는 MockLlmClient가 대신 동작함.
 */
public interface LlmClient {

    /**
     * 프롬프트를 받아서 LLM이 생성한 텍스트를 리턴.
     * @param prompt 각 도메인에서 만든 프롬프트
     * @return LLM이 생성한 결과 텍스트
     */
    String generateText(String prompt);
}