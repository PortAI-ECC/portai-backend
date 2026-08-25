package com.portai.domain.llm.service;

import com.portai.domain.generation.entity.Generation;
import com.portai.domain.generation.repository.GenerationRepository;
import com.portai.domain.generation.service.UserContextAggregator;
import com.portai.domain.llm.dto.FollowUpQuestionRequest;
import com.portai.domain.llm.dto.JobRecommendationRequest;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import com.portai.infra.llmclient.LlmClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final LlmClient llmClient;
    private final GenerationRepository generationRepository;
    private final UserContextAggregator userContextAggregator;

    /**
     * 선택한 분야와 현재까지 입력한 자유 텍스트를 바탕으로
     * 경험을 구체화할 수 있는 추가 질문을 생성한다.
     */
    public List<String> generateFollowUpQuestions(
            FollowUpQuestionRequest request) {

        String freeText = request.getFreeText();

        if (freeText == null || freeText.isBlank()) {
            freeText = "(아직 작성한 내용이 없음)";
        }

        String prompt = """
                사용자가 취업 포트폴리오에 등록할 경험을 작성하고 있다.

                [선택한 분야]
                %s

                [현재까지 작성한 내용]
                %s

                사용자가 경험을 더 구체적으로 작성할 수 있도록 도움이 되는
                확장 질문을 3개 생성하라.

                다음 규칙을 반드시 지켜라.
                1. 사용자가 아직 작성하지 않은 구체적인 정보를 물어본다.
                2. 역할, 행동, 사용 기술, 문제 해결 과정, 성과 중 중요한 내용을 질문한다.
                3. 한 줄에 질문 하나만 작성한다.
                4. 번호, 글머리표, 따옴표, 추가 설명을 작성하지 않는다.
                5. 질문 문장만 출력한다.
                """.formatted(request.getCategory(), freeText);

        String response = llmClient.generateText(prompt);
        return parseLineList(response, 3);
    }

    /**
     * 특정 생성 결과물과 사용자의 전체 경험 데이터를 바탕으로
     * 적합한 직무를 추천한다.
     */
    @Transactional(readOnly = true)
    public List<String> recommendJobs(
            Long userId,
            JobRecommendationRequest request) {

        Generation generation = generationRepository
                .findByIdAndUserId(request.getGenerationId(), userId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.GENERATION_NOT_FOUND));

        String generationText = generation.getResults().stream()
                .filter(result ->
                        result.getContent() != null
                                && !result.getContent().isBlank())
                .map(result ->
                        "- [" + result.getType().name() + "] "
                                + result.getContent())
                .collect(Collectors.joining("\n"));

        if (generationText.isBlank()) {
            generationText = "(생성된 결과물 내용이 없음)";
        }

        String userContext =
                userContextAggregator.buildUserContext(userId);

        String prompt = """
                다음 사용자의 경험 데이터와 생성된 취업 문서를 분석하여
                가장 적합한 직무 4개를 우선순위 순서로 추천하라.

                [사용자 전체 경험 데이터]
                %s

                [생성된 취업 문서]
                %s

                다음 규칙을 반드시 지켜라.
                1. 구체적인 직무명만 작성한다.
                2. 가장 적합한 직무부터 순서대로 작성한다.
                3. 한 줄에 직무 하나만 작성한다.
                4. 번호, 글머리표, 설명, 따옴표를 작성하지 않는다.
                5. 직무명 외의 문장은 출력하지 않는다.
                """.formatted(userContext, generationText);

        String response = llmClient.generateText(prompt);
        return parseLineList(response, 4);
    }

    /**
     * LLM이 줄 단위로 반환한 목록에서 번호와 글머리표를 제거한다.
     */
    private List<String> parseLineList(String response, int limit) {

        if (response == null || response.isBlank()) {
            return List.of();
        }

        return response.lines()
                .map(String::trim)
                .map(line ->
                        line.replaceFirst("^[-*•]\\s*", ""))
                .map(line ->
                        line.replaceFirst("^\\d+[.)]\\s*", ""))
                .map(line ->
                        line.replaceAll("^\"|\"$", ""))
                .filter(line -> !line.isBlank())
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
}