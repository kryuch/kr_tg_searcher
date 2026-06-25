package ru.kryuch.krtg.searcher.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kryuch.krtg.searcher.config.TelegramSettingsResolver;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.exception.TelegramClientException;
import ru.kryuch.krtg.searcher.integration.dto.SendBulkMessageRequest;
import ru.kryuch.krtg.searcher.util.PythonMessagesResponse;
import ru.kryuch.krtg.searcher.util.SendResponse;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramPythonClient {

    private final RestTemplate restTemplate;
    private final TelegramSettingsResolver settings;

    public ChatInfo[] findAllChats() {

        try {
            return restTemplate.getForObject(buildUri("/api/chats/all"), ChatInfo[].class);
        } catch (Exception e) {
            log.error("Failed to get chats", e);
            throw new RuntimeException("Python /api/chats/all failed", e);
        }
    }

    public List<ChatInfo> findChatsByIds(List<Long> chatIds) {
        String requestUrl =
                UriComponentsBuilder
                        .fromUri(buildUri("/api/chats/info"))
                        .build()
                        .toUriString();

            try {
                ChatInfo[]  response = restTemplate.postForObject(requestUrl, Map.of("chat_ids", chatIds), ChatInfo[].class);

                return (response == null)
                        ? List.of()
                        : Arrays.asList(response);
        } catch (Exception e) {
            log.error("Failed to search chats", e);
            throw new RuntimeException("Python /api/search failed", e);
        }
    }

    public ChatInfo[] searchChats(SearchParams params) {
        try {
            return restTemplate.postForObject(
                    buildUri("/api/search"),
                    params,
                    ChatInfo[].class
            );
        } catch (Exception e) {
            log.error("Failed to search chats", e);
            throw new RuntimeException("Python /api/search failed", e);
        }
    }

    public SendResponse sendBulkMessages(SendBulkMessageRequest request) {

        try {
            return restTemplate.postForObject(
                    buildUri("/api/send_bulk_messages"),
                    request,
                    SendResponse.class
            );
        } catch (Exception e) {
            log.error("Failed to send messages", e);
            throw new RuntimeException("Python send_bulk_messages failed", e);
        }
    }

    public PythonMessagesResponse getChatPreview(Long chatId, Integer limit) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/chat-preview/" + chatId));

        if (limit != null && limit > 0) {
            builder.queryParam("limit", limit);
        }

        try {
            return restTemplate.getForObject(
                    builder.toUriString(),
                    PythonMessagesResponse.class
            );
        } catch (Exception e) {
            log.error("Failed to get chat preview {}", chatId, e);
            throw new RuntimeException(e);
        }
    }

    private <T> T execute(
            Supplier<T> action,
            String operation
    ) {

        try {
            return action.get();

        } catch(Exception e) {
            log.error(operation, e);
            throw new RuntimeException(operation,e);
        }
    }

    private URI buildUri(String path) {
        String baseUrl = settings.getBaseUrl();

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new TelegramClientException(
                    "Python URL not configured"
            );
        }

        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(path)
                .build()
                .toUri();
    }


}