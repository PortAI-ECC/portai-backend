package com.portai.infra.llmclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * OpenAI Chat Completions API를 호출하는 LlmClient 구현체.
 * llm.provider=openai 일 때만 빈으로 등록됨 (application.yml 참고).
 *
 * 주의: gpt-5.6 계열은 추론(reasoning) 모델이라 temperature 등 샘플링 파라미터를
 * 커스텀 값으로 보내면 400 에러가 남. 그래서 temperature는 아예 보내지 않음.
 */
@Component
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "openai")
public class OpenAiClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final OpenAiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiClient(OpenAiProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .build();
    }

    @Override
    public String generateText(String prompt) {
        try {
            String requestBody = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + "/chat/completions"))
                    .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (response.statusCode() != 200) {
                // 로그 라인이 잘려도 원인을 알 수 있도록 body 길이와 전체 내용을 별도 줄로 남김
                log.error("OpenAI API 호출 실패. status={}", response.statusCode());
                log.error("OpenAI 응답 본문 (length={}): {}", body == null ? -1 : body.length(), body);
                throw new LlmClientException(
                        "OpenAI API 호출 실패 (status=" + response.statusCode() + ", body=" + body + ")");
            }

            return extractContent(body);

        } catch (LlmClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI 호출 중 예외 발생", e);
            throw new LlmClientException("OpenAI 호출 중 오류가 발생했습니다.", e);
        }
    }

    private String buildRequestBody(String prompt) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", properties.getModel());

        ArrayNode messages = root.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "너는 이공계 취업준비생의 커리어 문서 작성을 돕는 어시스턴트다. 제공된 정보 안에서만 사실대로 작성한다.");

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        // gpt-5.6 계열(추론 모델)은 temperature 커스텀 값을 지원하지 않아 400이 남.
        // 그래서 temperature 파라미터는 아예 넣지 않음 (기본값 사용).

        return root.toString();
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new LlmClientException("OpenAI 응답에 choices가 없습니다.");
            }
            return choices.get(0).path("message").path("content").asText();
        } catch (LlmClientException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmClientException("OpenAI 응답 파싱 실패", e);
        }
    }
}