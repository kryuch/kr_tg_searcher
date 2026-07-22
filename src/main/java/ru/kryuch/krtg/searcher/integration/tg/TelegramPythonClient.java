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
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.exception.TelegramClientException;
import ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequest;
import ru.kryuch.krtg.searcher.integration.dto.ChatResponse;
import ru.kryuch.krtg.searcher.integration.dto.InitRequest;
import ru.kryuch.krtg.searcher.integration.dto.RequestCodeResponse;
import ru.kryuch.krtg.searcher.integration.dto.SearchRequest;
import ru.kryuch.krtg.searcher.integration.dto.SendBulkMessageRequestByConcatUsername;
import ru.kryuch.krtg.searcher.integration.dto.SendBulkMessageRequestByContactId;
import ru.kryuch.krtg.searcher.integration.dto.UpdateFolderRequest;
import ru.kryuch.krtg.searcher.integration.dto.VerifyCodeResponse;
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

    public void init(InitRequest initRequest) {
        String response = execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/init"),
                        initRequest,
                        String.class
                ),
                "Failed to init"
        );
    }

    public List<FolderInfo> findAllFolders(Integer tgAccountId) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/folders"))
                .queryParam("accountId", tgAccountId);

        FolderInfo[] response = execute(
                () -> restTemplate.getForObject(
                        builder.build().toUri(),
                        FolderInfo[].class
                ),
                "Failed to get folders"
        );

        return response == null
                ? List.of() :
                List.of(response);
    }


    public List<ChatInfo> findAllChats(Integer tgAccountId) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/chats/all"))
                .queryParam("accountId", tgAccountId);

        ChatInfo[] response = execute(
                () -> restTemplate.getForObject(
                        builder.build().toUri(),
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

    public List<ChatResponse> searchChats(SearchRequest request) {
        log.info("TelegramPythonClient::searchChats (request = {}", request);
        ChatResponse[] response = execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/search"),
                        request,
                        ChatResponse[].class
                ),
                "Failed to search chats"
        );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public SendResponse sendBulkMessages(SendBulkMessageRequestByConcatUsername request) {
        log.info("TelegramPythonClient::sendBulkMessages (request = %s", request);
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

    public PythonMessagesResponse getChatPreview(Long chatId, Integer tgAccountId, Integer limit) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/chat-preview/"))
                .queryParam("chatId", chatId)
                .queryParam("accountId", tgAccountId);

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

    public RequestCodeResponse sendCode(Integer tgAccountId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(buildUri("/api/session/request_code"))
                .queryParam("accountId", tgAccountId);

        return execute(
                () -> restTemplate.postForObject(
                        buildUri("/api/session/request_code"),
                        tgAccountId,
                        RequestCodeResponse.class
                ),
                String.format(
                        "Failed to get code %s",
                        tgAccountId
                )
        );
    }

    public VerifyCodeResponse verify(VerifyTgCodeParam verifyTgCodeParam) {

        return execute(
                () ->restTemplate.postForObject(
                        buildUri("/api/session/verify_code"),
                        verifyTgCodeParam,
                        VerifyCodeResponse.class
                ),
                String.format(
                        "Failed to verify code %s",
                        verifyTgCodeParam.getTgAccountId()
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