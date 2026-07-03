package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSynchronizationService {

    private final TelegramMessagingGateway telegramMessagingGateway;

    private final FolderService folderService;
    private final ChatRepository chatRepository;

    public boolean synchr() {
        try {
            folderService.synchronize(true);

            telegramMessagingGateway.findAllChats()
                    .forEach(
                            item -> {
                                if (!chatRepository.existsById(item.getId())) {
                                    chatRepository.save(
                                            new ChatEntity(item.getId(), item.getUsername(), item.getName(), 0)
                                    );
                                }
                            }
                    );
            return true;
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e.getMessage());
            return false;
        }
    }
}
