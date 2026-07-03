package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.VacanciesContainer;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.integration.dto.ChatIdsRequest;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderRepository;
import ru.kryuch.krtg.searcher.type.ChatStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final FolderChatService folderChatService;
    private final TelegramMessagingGateway telegramMessagingGateway;
    private final ChatMapper chatMapper;

    private final VacancyService vacancyService;

    public List<ChatInfo> all() {
        try {
            log.info("Запрос всех чатов");
            return   telegramMessagingGateway.findAllChats();
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ChatInfo> search(SearchParams searchParams) {
        try {
            log.info("Поиск чатов");

            if (searchParams.getExcludeStatusFlag()) {
                searchParams.setExcludeChatIds(chatRepository.findIdsByStatusGreaterThan(0));
            }

            List<ChatInfo> result = telegramMessagingGateway.searchChats(searchParams).stream()
                    .map(item -> {
                        Optional<ChatEntity> chatEntity = chatRepository.findById(item.getId());
                        if (chatEntity.isPresent()) {
                            item.setStatus(ChatStatus.getChatStatus(chatEntity.get().getStatus()));
                        }
                        item.setFolders(folderChatService.getFoldersByChatId(item.getId()));

                        return item;
                    }).toList();
            log.info("Найдено чатов: {}", result != null ? result.size() : 0);

            return result;
        } catch (Exception e) {
            log.error("Ошибка при поиске чатов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public MessagesHistory messages(Long chatId, Integer limit) {
        try {
            MessagesHistory history = telegramMessagingGateway.getMessages(chatId, limit);
            history.setChatInfo(chatMapper.fromEntity(chatRepository.findById(chatId).orElse(new ChatEntity())));
            log.info("Получено сообщений для чата {}: {}", chatId, history.size());
            return history;
        } catch (Exception e) {
            log.error("Ошибка при получении сообщений чата {}: {}", chatId, e.getMessage());
            return new MessagesHistory();
        }
    }


    public VacanciesContainer createVacanciesContainer(Long chatId, Integer limit) {
        MessagesHistory messagesHistory = telegramMessagingGateway.getMessages(chatId, limit);
        messagesHistory.setChatInfo(chatMapper.fromEntity(chatRepository.findById(chatId).orElse(new ChatEntity())));
        log.info("Получено сообщений для чата {}: {}", chatId, messagesHistory.size());

        return vacancyService.analyze(messagesHistory);
    }


    public Boolean update(Long chatId, String username, String name, Integer status) {
        Optional<ChatEntity> chatEntity = chatRepository.findById(chatId);
        if (chatEntity.isPresent()) {
            chatEntity.get().setStatus(status);
            chatRepository.save(chatEntity.get());
        } else {
            chatRepository.save(new ChatEntity(chatId, username, name, status));
        }
        return true;
    }

    public Map<Long, String> getNamesByIds(List <Long> chatIds) {
        Map<Long, String> names =
                Streamable.of(chatRepository.findAllById(chatIds)).stream()
                        .filter(item -> item != null && item.getId() != null & item.getName() != null)
                        .collect(Collectors.toMap(ChatEntity::getId, ChatEntity::getName));

        List<Long> emptyIds =
                chatIds.stream()
                        .filter(item -> !names.containsKey(item) || Objects.isNull(names.get(item)) || names.get(item).isEmpty())
                        .toList();

        if (!CollectionUtils.isEmpty(emptyIds)) {
            telegramMessagingGateway.findChatsByIds(new ChatIdsRequest(emptyIds)).stream().forEach(item -> {
                names.put(item.getId(), item.getName());
            });
        }

        return names;
    }

}