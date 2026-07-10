package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.SendMessageParam;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
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

    private static final String FIRST_MESSAGE = "first_message";


    public List<ChatInfo> sendToChats(String message, List<Long> ids) {
/*
        Set<String> chats = Streamable.of(chatRepository.findAllById(ids))
                .stream()
                .map(ChatEntity::getUsername)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
*/


        return telegramMessagingGateway.sendMessage(
                message,
                new ChatIdsRequest(
                        StreamSupport.stream(chatRepository.findAllById(ids).spliterator(), false)
                                .map(item -> new ChatIdsRequestItem(item.getId(), item.getTgId()))
                                .toList()
                )
        );
    }

    public List<ChatInfo> registerAndSend(SendMessageParam sendMessageParam, Set<String> chats) {
        List<ChatInfo> chatDtos = telegramMessagingGateway.sendMessage(sendMessageParam, chats, true);
        chatRepository.saveAll(chatMapper.toEntityList(chatDtos));
        return chatDtos;
    }

    public List<ChatInfo> createNewContacts(String text) {
        return null;/*
        List<ChatInfo> chats =
                registerAndSend(settingService.getByCode(FIRST_MESSAGE).getValue(), newContactService.contacts(text));
        chats.forEach(chatStatusService::processSendResult);
        return chats;*/
    }


}
