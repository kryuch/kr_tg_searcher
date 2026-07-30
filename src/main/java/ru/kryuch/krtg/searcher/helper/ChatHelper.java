package ru.kryuch.krtg.searcher.helper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;
import ru.kryuch.krtg.searcher.entity.UserEntity;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.TgUserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class ChatHelper {

    private final ChatRepository chatRepository;
    private final TgUserRepository tgUserRepository;
    private final ChatMapper chatMapper;

    public ChatKey getChatKey(Long chatId) {
        ChatEntity chatEntity = chatRepository.findById(chatId).get();
        return new ChatKey(chatEntity.getUser().getId(), chatEntity.getTgId());
    }

    public Map<ChatKey, ChatEntity> getChatMap(List<Long> userIds) {
        return StreamSupport.stream(
                        chatRepository.findAllByUserIds(userIds).spliterator(),
                        false
                )
                .collect(Collectors.toMap(
                        chat -> new ChatKey(chat.getUser().getId(), chat.getTgId()),
                        Function.identity()
                ));
    }

    public Map<Long, ChatEntity> getChatMap(List<Long> userIds, Integer tgId) {
        return StreamSupport.stream(
                        chatRepository.findAllByTgIdAndUserIds(tgId, userIds).spliterator(),
                        false
                )
                .collect(Collectors.toMap(
                        chat -> chat.getUser().getId(),
                        Function.identity()
                ));
    }

    public Map<Long, TgUserEntity> getUserMap(List<Long> userIds) {
        return StreamSupport.stream(
                        tgUserRepository.findAllById(userIds).spliterator(),
                        false
                )
                .collect(Collectors.toMap(
                        TgUserEntity::getId,
                        Function.identity()
                ));
    }

    public Map <Long, Long> getChatUserMap(List<Long> chatIds) {
        return StreamSupport.stream(
                        chatRepository.findAllById(chatIds).spliterator(),
                        false
                )
                .collect(Collectors.toMap(
                        ChatEntity::getId,
                        item -> item.getUser().getId()
                ));
    }

    @Transactional
    public ChatKey createNewChat(ChatInfo chatInfo) {
        Optional <TgUserEntity> tgUserEntity = tgUserRepository.findById(chatInfo.getId());
        TgUserEntity user = (tgUserEntity.isEmpty()) ?
            tgUserRepository.save(TgUserEntity.builder().username(chatInfo.getUsername()).id(chatInfo.getId()).build()): tgUserEntity.get();

        Optional <ChatEntity> chatEntityOptional = chatRepository.findByUserIdAndTgId(chatInfo.getId(), chatInfo.getTgAccountId());
        if (chatEntityOptional.isEmpty())  {
            ChatEntity chatEntity = chatMapper.toEntity(chatInfo);
            chatEntity.setUser(user);
            chatRepository.save(chatEntity);
        }

        return new ChatKey(chatInfo.getId(), chatInfo.getTgAccountId());
    }
}
