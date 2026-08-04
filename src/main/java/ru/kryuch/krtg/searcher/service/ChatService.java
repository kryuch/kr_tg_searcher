package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.VacanciesContainer;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.mapper.ChatKeyMapper;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.type.ChatStatus;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final SettingService settingService;
    private final ChatHelper chatHelper;
    private final ChatKeyMapper chatKeyMapper;


/*
    public List<ChatInfo> all() {
        try {
            log.info("Запрос всех чатов");
            return   telegramMessagingGateway.findAllChats(1);
        } catch (Exception e) {
            log.error("Ошибка при получении чатов: {}", e);
            return Collections.emptyList();
        }
    }*/

    public List<ChatInfo> search(SearchParams searchParams, boolean withFolderFlag) {
        try {
            log.info("Поиск чатов");

            if (searchParams.getExcludeStatusFlag()) {
                searchParams.setExcludeChats(chatKeyMapper.fromEntityList(chatRepository.findKeysByStatusGreaterThan(0)));
            }

            String targetFolderTitle = settingService.getValueByCode(SettingConfig.TARGET_FOLDER_SETTING_CODE);
            List<ChatInfo> chatInfos = telegramMessagingGateway.searchChats(searchParams);

            List<Long> chatIds = chatInfos.stream().map(ChatInfo::getId).toList();

            Map<ChatKey, ChatEntity> chatsMap = chatHelper.getChatMap(chatIds);

            Map<Long, List<FolderInfo>> foldersMap = (withFolderFlag) ? folderChatService.getFoldersByChatIds(chatIds) : null;

            List<ChatInfo> result = chatInfos.stream()
                    .map(item -> {

                        ChatKey chatKey = new ChatKey(item.getId(), item.getTgAccountId());

                        if (chatsMap.containsKey(chatKey)) {
                            item.setStatus(ChatStatus.getChatStatus(chatsMap.get(chatKey).getStatus()));
                            item.setChatId(chatsMap.get(chatKey).getId());
                        }
                        if (withFolderFlag) {
                            item.setFolders(foldersMap.get(item.getId()));
                            item.setHasTargetFolder(
                                    (CollectionUtils.isEmpty(item.getFolders())) ? false :
                                            (item.getFolders().stream()
                                                    .filter(folder -> targetFolderTitle.equals(folder.getTitle()))
                                                    .findFirst().isPresent())
                            );
                        }
                        return item;
                    }).toList();
            log.info("Найдено чатов: {}", result != null ? result.size() : 0);

            return result;
        } catch (Exception e) {
            log.error("Ошибка при поиске чатов: {}", e);
            return Collections.emptyList();
        }
    }

    public MessagesHistory messages(Long chatId, Integer limit) {
        try {
            MessagesHistory messagesHistory = telegramMessagingGateway.getMessages(chatId, limit);
            messagesHistory.setChatInfo(chatMapper.fromEntity(chatRepository.findById(chatId).orElse(new ChatEntity())));
            log.info("Получено сообщений для чата {}: {}", chatId, messagesHistory.size());
            return messagesHistory;
        } catch (Exception e) {
            log.error("Ошибка при получении сообщений чата {}: {}", chatId, e);
            return new MessagesHistory();
        }
    }


    public VacanciesContainer createVacanciesContainer(Long chatId, Integer limit) {
        MessagesHistory messagesHistory = messages(chatId, limit);
        return vacancyService.analyze(messagesHistory);
    }


    public Boolean update(Long chatId, String username, String name, Integer status) {
        Optional<ChatEntity> chatEntity = chatRepository.findById(chatId);
        if (chatEntity.isPresent()) {
            chatEntity.get().setStatus(status);
            chatRepository.save(chatEntity.get());
        } else {
            //    chatRepository.save(new ChatEntity(chatId, username, name, status));
        }
        return true;
    }

    public Set<String> getUsernamesByTg(List <Integer> tgIds) {
        SearchParams searchParams =
                SearchParams.builder()
                        .botType(PersonalChatType.PERSONAL)
                        .term(settingService.getValueByCode(SettingConfig.TERM_SETTING_CODE))
                        .groupType(PersonalChatType.PERSONAL)
                        .tgAccountIds(tgIds)
                        .messagesCount(0)
                        .maxFoundCount(1024)
                        .excludeChats(chatKeyMapper.fromEntityList(chatRepository.findKeysByStatusEqual(3)))
                        .build();

        List<ChatInfo> chats = telegramMessagingGateway.searchChats(searchParams);
        return chats.stream().map(item -> item.getUsername()).collect(Collectors.toSet());
    }

    public Set<String> getUsernamesByIds(List<Long> chatIds) {
        return chatRepository.findEnrichedByIds(chatIds).stream().map(item -> item.getUser().getUsername()).collect(Collectors.toSet());
    }

}