package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

@Component
@RequiredArgsConstructor
public class ChatHelper {

    private final ChatRepository chatRepository;

    public Integer getChatTgAccountId(Long chatId) {
        return chatRepository.findById(chatId).get().getTgId();
    }
}
