package com.portai.infra.llmclient; // TODO: 실제 패키지명으로 변경

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
 *
 * 추가 Gradle 의존성 없이 Java 17 내장 java.net.http.HttpClient를 사용함.
 * Jackson(ObjectMapper)은 spring-boot-starter-web에 이미 포함되어 있어서 별도 추가 불필요.
 *
 * llm.provider=openai 일 때만 빈으로 등록됨 (application.yml 참고).
 * 로컬 개발 중에는 기본값(mock)이라 이 클라이언트가 아예 안 뜨고 API 호출/비용이 발생하지 않음.
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
    public String generate(String systemPrompt, String userPrompt) {
        try {
            String requestBody = buildRequestBody(systemPrompt, userPrompt);

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
                throw new LlmClientException(
                        "OpenAI API 호출 실패 (status=" + response.statusCode() + ")");
            }

            return extractContent(response.body());

        } catch (LlmClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI 호출 중 예외 발생", e);
            throw new LlmClientException("OpenAI 호출 중 오류가 발생했습니다.", e);
        }
    }

    private String buildRequestBody(String systemPrompt, String userPrompt) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", properties.getModel());

        ArrayNode messages = root.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

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
