package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSynchronizationService {

    private final TelegramMessagingGateway telegramMessagingGateway;
    private final FolderService folderService;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final ChatHelper chatHelper;

    public void synchronize(List<Integer> tgAccountIds) {
        tgAccountIds.forEach(item -> synchronize(item));
    }

    public boolean synchronize(Integer tgAccountId) {
        try {
            log.info("Synchronize account {}", tgAccountId);
            List<ChatInfo> chats = telegramMessagingGateway.findAllChats(tgAccountId);
            Map<Long, ChatEntity> chatMap = chatHelper.getChatMap(chats.stream().map(ChatInfo::getId).toList());
            List <ChatEntity> chatEntities = new ArrayList<>();

            for (ChatInfo chatInfo : chats) {

                ChatEntity chatEntity = chatMap.get(chatInfo.getId());

                if (chatEntity == null || chatEntity.getTgId() == null) {
                    if (chatEntity == null) {
                        chatEntity = chatMapper.toEntity(chatInfo);
                    }
                    chatEntity.setTgId(tgAccountId);
                    chatEntities.add(chatEntity);
                }
            }

            if (!chatEntities.isEmpty()) {
                chatRepository.saveAll(chatEntities);
            }

            folderService.synchronize(tgAccountId, true);
            log.info("Saved {} chats", chatEntities.size());
            return true;
        } catch (Exception e) {
            log.error("Ошибка синхронизации аккаунта {}", tgAccountId, e);
            return false;
        }
    }

}
