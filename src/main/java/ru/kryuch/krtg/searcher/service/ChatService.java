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
import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.helper.MessagesHelper;
import ru.kryuch.krtg.searcher.helper.TelegramService;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final IgnoreRepository ignoreRepository;
    private final SettingService settingService;
    private final TelegramService telegramService;
    private final ChatMapper chatMapper;

    public Boolean synchr() {
        try {
            telegramService.getAll()
                    .forEach(
                            item -> {
                                System.out.println(item.getId() + "\t" + item.getUsername() + "\t" + item.getName());
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


    public List<ChatInfo> all() {
        try {
            log.info("Запрос всех чатов");
            return telegramService.getAll();
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<ChatInfo> createNewContacts(String text) {
        String message = settingService.getByCode("first_message").getValue();
        List<ChatInfo> chats = telegramService.sendMessage(message, contacts(text), true);
        chats.stream().forEach(item -> {
            if (item.getStatus().equals(13)) {
                item.setStatus(0);
                chatRepository.save(chatMapper.toEntity(item));
            }
            if (item.getStatus().equals(12)) {
                item.setStatus(4);
                chatRepository.save(chatMapper.toEntity(item));
            }
        });
        return chats;
    }

    private Set<String> contacts(String text) {
        Set<String> result = new HashSet<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String username = "@" + matcher.group(1);
            if (!chatRepository.existsByUsername(username) && !ignoreRepository.existsByUsername(matcher.group(1))) {
                result.add(username);
            }
        }
        return result;
    }

    public List<ChatInfo> search(SearchParams searchParams) {
        try {
            log.info("Поиск чатов");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (searchParams.getExcludeStatusFlag()) {
                searchParams.setExcludeChatIds(chatRepository.findIdsByStatusGreaterThan(0));
            }

            List<ChatInfo> result = telegramService.search(searchParams).stream()
                    .map(item -> {
                        Optional<ChatEntity> chatEntity = chatRepository.findById(item.getId());
                        if (chatEntity.isPresent()) {
                            item.setStatus(chatEntity.get().getStatus());
                        }
                        return item;
                    }).toList();
            log.info("Найдено чатов: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("Ошибка при поиске чатов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public List<ChatInfo> sendMessage(String message, List<Long> ids) {
        Set<String> chats = ids.stream().map(item -> {
            Optional<ChatEntity> chatEntity = chatRepository.findById(item);
            return (chatEntity.isPresent()) ? chatEntity.get().getName() : null;
        }).collect(Collectors.toSet());

        return telegramService.sendMessage(message, chats, false);
    }


    public List<ChatInfo> sendMessage(String message, Set<String> chats) {
        List<ChatInfo> chatDtos = telegramService.sendMessage(message, chats, true);
        chatRepository.saveAll(chatMapper.toEntityList(chatDtos));
        return chatDtos;
    }

    public MessagesHistory messages(Long chatId, Integer limit) {
        try {

            MessagesHistory history = telegramService.getMessages(chatId, limit);
            history.setChatInfo(chatMapper.fromEntity(chatRepository.findById(chatId).orElse(new ChatEntity())));
            log.info("Получено сообщений для чата {}: {}", chatId, history.size());
            return history;
        } catch (Exception e) {
            log.error("Ошибка при получении сообщений чата {}: {}", chatId, e.getMessage());
            return new MessagesHistory();
        }
    }


    public VacanciesContainer createVacanciesContainer(Long chatId, Integer limit) {
        MessagesHistory messagesHistory = telegramService.getMessages(chatId, limit);
        messagesHistory.setChatInfo(chatMapper.fromEntity(chatRepository.findById(chatId).orElse(new ChatEntity())));
        log.info("Получено сообщений для чата {}: {}", chatId, messagesHistory.size());

        VacanciesContainer vacanciesContainer = new VacanciesContainer();
        vacanciesContainer.setMessagesHistory(messagesHistory);
        Set<String> newTg = new HashSet<>();
        String term = settingService.getValueByCode("text_in_vacancy");

        vacanciesContainer.setVacancies(
                messagesHistory.getValues().stream()
                        .filter(item -> Objects.nonNull(item))
                        .map(message -> {
                            System.out.println("************************************");
                            System.out.println(message.getValue());
                            System.out.println("**");
                                    VacancyInfo vacancyInfo =
                                            MessagesHelper.createVacancyInfo(message.getValue(), message.getDateTime());
                                    System.out.println(vacancyInfo);
                                    if (Objects.isNull(vacancyInfo)) return null;
                            System.out.println("\t"+vacancyInfo.getTg());
                                    if (Objects.nonNull(vacancyInfo.getTg())) {
                                        if (chatRepository.existsByUsername(vacancyInfo.getTg().substring(1))) {
                                            vacancyInfo.setStatus(1);
                                            System.out.println("\t\t 1");
                                        } else {
                                            vacancyInfo.setStatus(2);
                                            System.out.println("\t\t 2 " + term);
                                            if (Objects.nonNull(term)) {
                                                System.out.println("\t\t\t " + (message.getValue().indexOf(term) > 0) + "\t" + message.getValue());
                                                if (message.getValue().indexOf(term) > 0) {
                                                    newTg.add(vacancyInfo.getTg());
                                                }
                                            }
                                            else {
                                                newTg.add(vacancyInfo.getTg());
                                            }
                                        }
                                    }
                                    return vacancyInfo;
                                }
                        )
                        .filter(item -> Objects.nonNull(item) && Objects.nonNull(item.getTitle()))
                        .toList()
        );

        vacanciesContainer.setNewTg(newTg);
        return vacanciesContainer;
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
            telegramService.getChatsInfo(emptyIds).stream().forEach(item -> {
                names.put(item.getId(), item.getName());
            });
        }

        return names;
    }

}