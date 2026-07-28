package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class ChatHelper {

    private final ChatRepository chatRepository;

    public Integer getChatTgAccountId(Long chatId) {
        return chatRepository.findById(chatId).get().getTgId();
    }

    public Map<Long, ChatEntity> getChatMap(List<Long> chatIds) {
        return StreamSupport.stream(
                        chatRepository.findAllById(chatIds).spliterator(),
                        false
                )
                .collect(Collectors.toMap(
                        ChatEntity::getId,
                        Function.identity()
                ));
    }
}
