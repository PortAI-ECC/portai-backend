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

            if (response.statusCode() != 200) {
                log.error("OpenAI API 호출 실패. status={}, body={}", response.statusCode(), response.body());
                throw new LlmClientException("OpenAI API 호출 실패 (status=" + response.statusCode() + ")");
            }

            return extractContent(response.body());

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

        root.put("temperature", 0.7);

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