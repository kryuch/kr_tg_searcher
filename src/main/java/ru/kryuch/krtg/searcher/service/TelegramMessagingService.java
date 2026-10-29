package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.List;
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
    private final FolderHelper folderHelper;


    public List<ChatResponse> sendToChats(String message, boolean clearPrevious, List<Long> ids) {

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

    public List<ChatResponse> registerAndSend(SendMessageParam sendMessageParam, Set<String> chats) {

        log.info("TelegramMessagingService::registerAndSend (sendMessageParam={}, chats = {}", sendMessageParam, chats);

        List<ChatResponse> chatDtos = telegramMessagingGateway.sendMessage(sendMessageParam, chats, true);

        chatDtos.stream().forEach(chatDto -> {
            chatHelper.createNewChat(chatMapper.fromResponse(chatDto));
        });

        folderHelper.setToFolderAfterSending(sendMessageParam, chatDtos);

        return chatDtos;
    }

    public List<ChatResponse> createNewContacts(String text, Integer tgId) {
        SendMessageParam sendMessageParam =
                SendMessageParam.builder().message(
                        settingService.getByCode(SettingConfig.FIRST_MESSAGE_SETTING_CODE).getValue())
                        .tgAccountId(tgId)
                        .build();
        List<ChatResponse> chats =
                registerAndSend(sendMessageParam, newContactService.contacts(text));
        chats.forEach(chatStatusService::processSendResult);
        return chats;
    }


}
