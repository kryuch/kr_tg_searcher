package ru.kryuch.krtg.searcher.integration.tg;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.integration.tg.dto.ChatIdsRequest;
import ru.kryuch.krtg.searcher.integration.tg.dto.SendBulkMessageRequestByContactId;
import ru.kryuch.krtg.searcher.util.PythonMessagesResponse;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TelegrammRecruterClient {

    private final TelegramPythonClient telegramPythonClient;

    private ChatKey chatKey;

    @PostConstruct
    protected void postConstruct() {
        chatKey = new ChatKey(8491767152L, 2);
    }

    public Message getMessage() {
        PythonMessagesResponse pythonMessagesResponse = telegramPythonClient.getChatPreview(chatKey, 1);
        return pythonMessagesResponse.getMessages().get(0);
    }

    public void sendMessage(String message) {
        SendBulkMessageRequestByContactId sendBulkMessageRequestByContactId =
                SendBulkMessageRequestByContactId.builder()
                        .messageText(message)
                        .contacts(
                                new ChatIdsRequest(
                                        List.of(
                                                ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequestItem.builder()
                                                        .id(chatKey.getUserId())
                                                        .tgAccountId(chatKey.getTgAccountId())
                                                        .build()
                                        )
                                )
                        )
                        .clearPrevious(false)
                        .delaySeconds(1)
                        .build();
        telegramPythonClient.sendBulkMessages(sendBulkMessageRequestByContactId);
    }

}
