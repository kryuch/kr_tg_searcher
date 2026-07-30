package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.SendMessageParam;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequest;
import ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequestItem;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TelegramMessagingService {

    private final TelegramMessagingGateway telegramMessagingGateway;
    private final ChatRepository chatRepository;
    private final NewContactService newContactService;
    private final ChatMapper chatMapper;
    private final SettingService settingService;
    private final ChatStatusService chatStatusService;
    private final ChatHelper chatHelper;

    private static final String FIRST_MESSAGE = "first_message";


    public List<ChatInfo> sendToChats(String message, boolean clearPrevious, List<Long> ids) {

        return telegramMessagingGateway.sendMessage(
                message,
                clearPrevious,
                new ChatIdsRequest(
                        StreamSupport.stream(chatRepository.findAllById(ids).spliterator(), false)
                                .map(item -> new ChatIdsRequestItem(item.getUser().getId(), item.getTgId()))
                                .toList()
                )
        );
    }

    public List<ChatInfo> registerAndSend(SendMessageParam sendMessageParam, Set<String> chats) {
        List<ChatInfo> chatDtos = telegramMessagingGateway.sendMessage(sendMessageParam, chats, true);
        chatDtos.stream().forEach(chatDto -> {
            chatHelper.createNewChat(chatDto);
        });
        return chatDtos;
    }

    public List<ChatInfo> createNewContacts(String text, Integer tgId) {
        SendMessageParam sendMessageParam =
                SendMessageParam.builder().message(
                        settingService.getByCode(FIRST_MESSAGE).getValue())
                        .tgAccountId(tgId)
                        .build();
        List<ChatInfo> chats =
                registerAndSend(sendMessageParam, newContactService.contacts(text));
        chats.forEach(chatStatusService::processSendResult);
        return chats;
    }


}
