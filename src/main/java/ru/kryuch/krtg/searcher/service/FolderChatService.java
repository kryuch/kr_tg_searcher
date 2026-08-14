package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.entity.FolderChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.integration.tg.dto.FolderChatIdsRequestItem;
import ru.kryuch.krtg.searcher.integration.tg.dto.UpdateFolderRequest;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderChatService {

    private final FolderChatRepository folderChatRepository;
    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;
    private final TelegramPythonClient telegramPythonClient;
    private final ChatHelper chatHelper;


    public Map<Long, List<FolderInfo>> getFoldersByChatIds(List<Long> chatIds) {
        if (chatIds == null || chatIds.isEmpty()) {
            return new HashMap<>();
        }

        return folderChatRepository.findByChatUserIdsGrouped(chatIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(FolderChatEntity::getFolder)
                                .map(folderMapper::fromEntity)
                                .collect(Collectors.toList())
                ));
    }

    public boolean updateLinksToTarget(List<Long> chatUserIds, Integer tgId, Boolean status) {
        log.info("FolderChatService::updateLinksToTarget (chatUserIds={}, tg={}, status={}", chatUserIds, tgId, status.toString());

        FolderEntity folderEntity = folderRepository.findTargetFolder(tgId)
                .orElseThrow(() ->
                        new IllegalStateException("Не настроена целевая папка"));

        Map<Long, Boolean> existingLinks = folderChatRepository.existsByFolderIdAndChatUserIds(folderEntity.getId(), chatUserIds);

        List<FolderChatEntity> folderChatEntities =
                chatUserIds.stream()
                        .filter(chatId -> existingLinks.getOrDefault(chatUserIds, false) != status)
                        .map(item -> new FolderChatEntity(folderEntity.getId(), item))
                        .toList();

        log.info("FolderChatService::updateLinksToTarget (folderChatEntities.size={}", folderChatEntities.size());
        if (folderChatEntities.isEmpty()) {
            return true;
        }

        if (status) {
            folderChatRepository.saveAll(folderChatEntities);
        } else {
            folderChatRepository.deleteAll(folderChatEntities);
        }


        telegramPythonClient.updateFolder(
                UpdateFolderRequest.builder()
                        .items(
                                folderChatEntities.stream().map(item ->
                                        FolderChatIdsRequestItem.builder()
                                                .folderId(folderEntity.getId())
                                                .id(item.getChatUserId())
                                                .tgAccountId(folderEntity.getTgId())
                                                .build()
                                ).toList()
                        )
                        .addOperationFlag(status)
                        .build()
        );

        return true;
    }

}
