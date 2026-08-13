package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.mapper.ChatUserMapper;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.TgUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSynchronizationService {

    private final TelegramMessagingGateway telegramMessagingGateway;
    private final FolderService folderService;
    private final ChatRepository chatRepository;
    private final TgUserRepository tgUserRepository;
    private final ChatMapper chatMapper;
    private final ChatUserMapper chatUserMapper;
    private final ChatHelper chatHelper;

    public void synchronize(List<Integer> tgAccountIds) {
        tgAccountIds.forEach(item -> synchronize(item));
    }

    public boolean synchronize(Integer tgAccountId) {
        try {
            log.info("Synchronize account {}", tgAccountId);
            List<ChatInfo> chats = telegramMessagingGateway.findAllChats(tgAccountId);

            Map<Long, ChatEntity> chatMap = chatHelper.getChatMap(chats.stream().map(ChatInfo::getId).toList(), tgAccountId);
            chatMap.putAll(chatHelper.getChatMap(chats.stream().map(ChatInfo::getId).toList(), null));

            Map<Long, TgUserEntity> tgUserMap = chatHelper.getUserMap(chats.stream().map(ChatInfo::getId).toList());

            List <ChatEntity> chatEntities = new ArrayList<>();
            List <TgUserEntity> tgUserEntities = new ArrayList<>();

            for (ChatInfo chatInfo : chats) {

                ChatEntity chatEntity = chatMap.get(chatInfo.getId());
                if (chatEntity == null || chatEntity.getTgId() == null) {
                    if (chatEntity == null) {
                        chatEntity = chatMapper.toEntity(chatInfo);
                    }
                    chatEntity.setTgId(tgAccountId);
                    chatEntities.add(chatEntity);
                }

                TgUserEntity tgUserEntity = tgUserMap.get(chatInfo.getId());
                if (chatEntity.getUser() == null) {
                    if (tgUserEntity == null) {
                        tgUserEntity = chatUserMapper.toEntity(chatInfo);
                        tgUserEntities.add(tgUserEntity);
                    }

                    chatEntity.setUser(tgUserEntity);
                }
                else {
                    // если пользователь есть, но не сохранилось почему-то username
                    if (!Objects.equals(tgUserEntity.getUsername(), chatInfo.getUsername()) || !Objects.equals(tgUserEntity.getName(), chatInfo.getName())) {
                        tgUserEntity.setUsername(chatInfo.getUsername());
                        tgUserEntity.setName(chatInfo.getName());
                        tgUserEntities.add(tgUserEntity);
                    }
                }
            }

            if (!tgUserEntities.isEmpty()) {
                tgUserRepository.saveAll(tgUserEntities);
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
