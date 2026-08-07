package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;
import ru.kryuch.krtg.searcher.repository.TgUserRepository;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatAccessHelper {

    private final TgUserRepository tgUserRepository;
    private final ChatRepository chatRepository;
    private final TgAccountRepository tgAccountRepository;


    public Set<String> findUniqUsername(Set<String> usernames, boolean uniqFlag) {
        Set <TgUserEntity> tgUserEntities = tgUserRepository.findAllByUsernameIn(usernames);

        List<ChatEntity> chats = chatRepository.findByUserIdsAndTgIds(
                tgUserEntities.stream().map(TgUserEntity::getId).toList(),
                tgAccountRepository.findAllByUserId(getCurrentUserId()).stream().map(TgAccountEntity::getId).toList()
        );

        log.info("ChatAccessHelper::findUniqUsername (chats={}", chats);

        Set <Long> existingUserIds = chats.stream().map(item -> item.getUser().getId()).collect(Collectors.toSet());
        log.info("ChatAccessHelper::findUniqUsername (existingUserIds={}", existingUserIds);
        return
                tgUserEntities.stream()
                        .filter(item -> existingUserIds.contains(item.getId()) != uniqFlag)
                        .map(item -> UserUtil.normalizeUsername(item.getUsername()).toLowerCase())
                        .collect(Collectors.toSet());
    }


    private CurrentUser getCurrentUser() {
        return UserUtil.getCurrentUser();
    }

    private Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }
}