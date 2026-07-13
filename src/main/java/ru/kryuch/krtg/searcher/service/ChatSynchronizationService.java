package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSynchronizationService {

    private final TelegramMessagingGateway telegramMessagingGateway;

    private final FolderService folderService;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public void synchr(List<Integer> tgAccountIds) {
        tgAccountIds.forEach(item -> synchr(item));
    }

    public boolean synchr(Integer tgAccountId) {
        try {
            telegramMessagingGateway.findAllChats(tgAccountId)
                    .forEach(
                            item -> {
                                Optional<ChatEntity> optionalChat = chatRepository.findById(item.getId());

                                if (optionalChat.isEmpty()) {
                                    ChatEntity chatEntity = chatMapper.toEntity(item);
                                    chatEntity.setTgId(tgAccountId);
                                    chatRepository.save(chatEntity);
                                } else {
                                    if (optionalChat.get().getTgId() == null) {
                                        optionalChat.get().setTgId(tgAccountId);
                                        chatRepository.save(optionalChat.get());
                                    }
                                }
                            }
                    );

            folderService.synchronize(tgAccountId, true);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e.getMessage());
            return false;
        }
    }
}
