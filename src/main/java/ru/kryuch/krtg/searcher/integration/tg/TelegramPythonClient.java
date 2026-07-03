package ru.kryuch.krtg.searcher.integration.tg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kryuch.krtg.searcher.config.TelegramSettingsResolver;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.exception.TelegramClientException;
import ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequest;
import ru.kryuch.krtg.searcher.integration.dto.SendBulkMessageRequestByConcatUsername;
import ru.kryuch.krtg.searcher.integration.dto.SendBulkMessageRequestByContactId;
import ru.kryuch.krtg.searcher.integration.dto.UpdateFolderRequest;
import ru.kryuch.krtg.searcher.util.PythonMessagesResponse;
import ru.kryuch.krtg.searcher.util.SendResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramPythonClient {

    private final RestTemplate restTemplate;
    private final TelegramSettingsResolver settings;

    public List<FolderInfo> findAllFolders() {

        FolderInfo[] response = execute(
                () -> restTemplate.getForObject(
                        buildUri("/api/folders"),
                        FolderInfo[].class
                ),
                "Failed to get folders"
        );

        return response == null
                ? List.of() :
                List.of(response);
    }


    public List<ChatInfo> findAllChats() {

        ChatInfo[] response = execute(
                () -> restTemplate.getForObject(
                        buildUri("/api/chats/all"),
                        ChatInfo[].class
                ),
                "Failed to get chats"
        );

        return response == null
                ? List.of() :
                List.of(response);
    }

    public List<ChatInfo> findChatsByIds(ChatIdsRequest request) {
        ChatInfo[] response = execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/chats/info"),
                        request,
                        ChatInfo[].class
                ),
                "Failed to get chats info"
        );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public List<ChatInfo> searchChats(SearchParams params) {
        ChatInfo[] response = execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/search"),
                        params,
                        ChatInfo[].class
                ),
                "Failed to search chats"
        );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public SendResponse sendBulkMessages(SendBulkMessageRequestByConcatUsername request) {
        return execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/send_bulk_messages"),
                        request,
                        SendResponse.class
                ),
                "Failed to send messages"
        );
    }

    public SendResponse sendBulkMessages(SendBulkMessageRequestByContactId request) {
        return execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/send_bulk_messages"),
                        request,
                        SendResponse.class
                ),
                "Failed to send messages"
        );
    }

    public PythonMessagesResponse getChatPreview(Long chatId, Integer limit) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/chat-preview/" + chatId));

        if (limit != null && limit > 0) {
            builder.queryParam("limit", limit);
        }
        return execute(
                () -> restTemplate.getForObject(
                        builder.build().toUri(),
                        PythonMessagesResponse.class
                ),
                String.format(
                        "Failed to get chat preview %s",
                        chatId
                )
        );
    }

    private <T> T execute(
            Supplier<T> action,
            String operation
    ) {

        try {
            return action.get();

        } catch (Exception e) {
            log.error(operation, e);
            throw new TelegramClientException(operation, e);
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
                .path(path.startsWith("/") ? path : "/" + path)
                .build()
                .toUri();
    }

    public Map<String, Object> updateFolder(UpdateFolderRequest request) {

        Map<String, Object> response = execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/folders/update"),
                        request,
                        Map.class
                ),
                "Failed to get chats info"
        );

        return response;/* == null
                ? List.of()
                : List.of(response);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/folders/update"));

        return execute(
                () -> restTemplate.getForObject(
                        builder.build().toUri(),
                        PythonMessagesResponse.class
                ),
                String.format(
                        "Failed to get chat preview %s",
                        chatId
                )
        );



        String url = getBaseUrl() + "/api/folders/update";

        Map<String, Object> request = Map.of(
                "folder_id", folderId,
                "chat_ids", chatIds,
                "add_to_folder", addToFolder
        );

        return restTemplate.postForObject(url, request, Map.class);*/
    }
}