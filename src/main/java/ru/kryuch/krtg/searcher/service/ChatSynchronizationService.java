package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.List;

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
                                if (!chatRepository.existsById(item.getId())) {
                                    ChatEntity chatEntity = chatMapper.toEntity(item);
                                    chatEntity.setTgId(tgAccountId);
                                    chatRepository.save(chatEntity);
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
