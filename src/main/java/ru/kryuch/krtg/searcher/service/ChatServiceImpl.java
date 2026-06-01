package ru.kryuch.krtg.searcher.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.helper.MessagesHelper;
import ru.kryuch.krtg.searcher.helper.TgHelper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final TgHelper tgHelper;
    private final RestTemplate restTemplate;
    private final String pythonHost;
    private final int pythonPort;

    public ChatServiceImpl(
            ChatRepository chatRepository, TgHelper tgHelper, RestTemplate restTemplate,
            @Value("${telegram.python.host:localhost}") String pythonHost,
            @Value("${telegram.python.port:8081}") int pythonPort) {
        this.chatRepository = chatRepository;
        this.tgHelper = tgHelper;
        this.restTemplate = restTemplate;
        this.pythonHost = pythonHost;
        this.pythonPort = pythonPort;
    }

    private String getBaseUrl() {
        return "http://" + pythonHost + ":" + pythonPort;
    }

    @Override
    public Boolean synchr() {
        try {
            String url = getBaseUrl() + "/api/chats/all";
            log.info("Запрос всех чатов: {}", url);

            Arrays.stream(restTemplate.getForObject(url, ChatInfo[].class))
                    .forEach(
                            item -> {
                                if (!chatRepository.existsById(item.getId())) {
                                    chatRepository.save(
                                            new ChatEntity(item.getId(), item.getName(), 0)
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

    @Override
    public List<ChatInfo> all() {
        try {
            String url = getBaseUrl() + "/api/chats";
            log.info("Запрос всех чатов: {}", url);

            ChatInfo[] chats = restTemplate.getForObject(url, ChatInfo[].class);
            return chats != null ? Arrays.asList(chats) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChatInfo> createNewContacts(String text) {
        String message = "Добрый день. Скажите, пожалуйста, у вас есть вакансии по Java-разработке";
        List <String> s = tgHelper.sendMessage(message, contacts(text), true);
        return null;
    }

    private Set<String> contacts(String text) {
        Set<String> result = new HashSet<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String username = "@" + matcher.group(1);
            if (!chatRepository.existsByName(username)) {
                result.add(username);
            }
        }
        return result;
    }

    @Override
    public List<ChatInfo> search(SearchParams searchParams) {
        try {
            String url = getBaseUrl() + "/api/search";
            log.info("Поиск чатов: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SearchParams> httpEntity = new HttpEntity<>(searchParams, headers);
            List<ChatInfo> result = Arrays.stream(restTemplate.postForObject(url, httpEntity, ChatInfo[].class))
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

    public MessagesHistory messages(Long chatId, Integer limit) {
        try {
            String url = getBaseUrl() + "/api/chat-preview/" + chatId;
            if (limit != null && limit > 0) {
                url += "?limit=" + limit;
            }
            log.info("Запрос сообщений чата {}: {}", chatId, url);

            PythonMessagesResponse response = restTemplate.getForObject(url, PythonMessagesResponse.class);

            if (response == null || response.getMessages() == null) {
                return new MessagesHistory();
            }

            MessagesHistory history = new MessagesHistory();
            for (PythonMessage pythonMsg : response.getMessages()) {
                history.add(new Message(parseDateTime(pythonMsg.getDateStr()), pythonMsg.getText(), pythonMsg.isMe()));
            }

            log.info("Получено сообщений для чата {}: {}", chatId, history.size());
            return history;
        } catch (Exception e) {
            log.error("Ошибка при получении сообщений чата {}: {}", chatId, e.getMessage());
            return new MessagesHistory();
        }
    }

    @Override
    public List<VacancyInfo> vacancies(MessagesHistory messagesHistory) {
        return messagesHistory.getValues().stream()
                .filter(item -> Objects.nonNull(item))
                .map(message -> {
                            VacancyInfo vacancyInfo =
                                    MessagesHelper.createVacancyInfo(message.getValue(), message.getDateTime());
                            if (Objects.isNull(vacancyInfo)) return null;
                            if (Objects.nonNull(vacancyInfo.getTg())) {
                                if (chatRepository.existsByName(vacancyInfo.getTg())) {
                                    vacancyInfo.setStatus(1);
                                } else {
                                    vacancyInfo.setStatus(2);
                                }
                            }
                            return vacancyInfo;
                        }
                )
                .filter(item -> Objects.nonNull(item) && Objects.nonNull(item.getTitle()))
                .toList();
    }

    @Override
    public Boolean sendToVacancyOwners(MessagesHistory messagesHistory, String term, String text) {
        Set<String> owners =
                messagesHistory.getValues().stream()
                        .filter(item -> Objects.nonNull(item) && item.getValue().indexOf(term) > 0)
                        .map(message -> {
                                    VacancyInfo vacancyInfo =
                                            MessagesHelper.createVacancyInfo(message.getValue(), message.getDateTime());
                                    if (Objects.isNull(vacancyInfo)) return null;

                                    String tg = vacancyInfo.getTg();
                                    if (Objects.isNull(tg) || chatRepository.existsByName(tg)) return null;
                                    return tg.replace("@", "");
                                }
                        )
                        .filter(item -> Objects.nonNull(item))
                        .collect(Collectors.toSet());

        // Отправляем сообщения
        String sendUrl = getBaseUrl() + "/api/send_bulk_messages";
        Map<String, Object> request = Map.of(
                "chat_ids", new ArrayList<>(owners),
                "message_text", text,
                "delay_seconds", 3
        );

        SendResponse sendResponse = restTemplate.postForObject(sendUrl, request, SendResponse.class);

        if (sendResponse != null && sendResponse.getResults() != null) {
            for (SendResult chatResult : sendResponse.getResults()) {
                if (chatResult.getStatus().equals("success")) {
                    // Сохраняем в БД
                    if (!chatRepository.existsById(chatResult.getId())) {
                        ChatEntity entity = new ChatEntity();
                        entity.setId(chatResult.getId());
                        entity.setName(chatResult.getName());
                        entity.setStatus(0);
                        chatRepository.save(entity);
                        log.info("Сохранён чат: {} ({})", chatResult.getName(), chatResult.getId());
                    }
                } else {
                    log.warn("Не удалось отправить сообщение: {}", chatResult.getError());
                }
            }
        }
        return true;
    }

    @Override
    public Boolean update(Long chatId, String name, Integer status) {
        Optional<ChatEntity> chatEntity = chatRepository.findById(chatId);
        if (chatEntity.isPresent()) {
            chatEntity.get().setStatus(status);
            chatRepository.save(chatEntity.get());
        } else {
            chatRepository.save(new ChatEntity(chatId, name, status));
        }
        return true;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("Ошибка парсинга даты: {}", dateStr);
            return null;
        }
    }

    @Data
    private static class SearchRequest {
        private String keyword;
        private int limit;
        private Integer maxChats;
        private boolean excludeBots;
        private boolean onlyChats;
        private Integer inactiveDays;
    }

    @Data
    private static class PythonMessagesResponse {
        private List<PythonMessage> messages;
    }

    @Data
    private static class PythonMessage {
        private String text;

        @JsonProperty("date_str")
        private String dateStr;

        @JsonProperty("is_me")
        private boolean isMe;
    }
}

@Data
class SendResponse {
    private int total;
    private int success;
    private int error;
    private List<SendResult> results;
}

@Data
class SendResult {
    private Long id;
    private String name;
    private String username;
    private String status;
    private String error;
}