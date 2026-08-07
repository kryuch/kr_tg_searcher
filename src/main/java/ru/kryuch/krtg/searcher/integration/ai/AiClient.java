package ru.kryuch.krtg.searcher.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

public class AiClient {

    private final WebClient webClient;
    private final String systemPrompt = "Ответы давай на русском языке, используя ТОЛЬКО обычный текст. Запрещено использовать Markdown, HTML, жирный шрифт, курсив, списки, заголовки или любые другие теги форматирования. Твой ответ должен быть готов для отображения в виде простого текста.";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String model;

    public AiClient(String apiKey, String model) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .build();
    }

    // Отправить сообщение
    public String sendMessage(String userMessage) {
        try {
            Map<String, Object> request = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    )
            );

            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);
            return json.path("choices").path(0).path("message").path("content").asText();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка: " + e.getMessage(), e);
        }
    }
}