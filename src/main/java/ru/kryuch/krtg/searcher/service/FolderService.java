package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderEntity;
import ru.kryuch.krtg.searcher.helper.FolderHelper;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final ChatRepository chatRepository;
    private final FolderChatRepository folderChatRepository;
    private final TelegramPythonClient telegramPythonClient;
    private final FolderMapper folderMapper;
    private final SettingService settingService;
    private final FolderHelper folderHelper;

    private String targetFolderTitle;

    @Transactional
    public void synchronize(Integer tgAccountId, boolean forceFlag) {

        targetFolderTitle = settingService.getValueByCode(SettingConfig.TARGET_FOLDER_SETTING_CODE);

        if (forceFlag) {
            folderRepository.deleteByTgId(tgAccountId);
        }

        List<FolderInfo> folders = telegramPythonClient.findAllFolders(tgAccountId);

        if (folders.isEmpty()) {
            return;
        }

        List<Integer> folderIds = folders.stream()
                .map(FolderInfo::getId)
                .collect(Collectors.toList());

        List<FolderEntity> foldersToSave =
                buildFoldersToSave(folders, folderHelper.getFolderMap(folderIds), tgAccountId);

        folderRepository.saveAll(foldersToSave);

        synchronizeAllFolderChats(
                folders.stream()
                        .collect(Collectors.toMap(
                                FolderInfo::getId,
                                Function.identity()
                        )));
    }

    private void synchronizeAllFolderChats(Map<Integer, FolderInfo> folderInfoMap) {
        if (folderInfoMap.isEmpty()) {
            return;
        }
        Set<Long> allChatIds = folderInfoMap.values().stream()
                .filter(folder -> folder.getChatIds() != null)
                .flatMap(folder -> folder.getChatIds().stream())
                .collect(Collectors.toSet());

        Set<Long> existingChatIds = new HashSet<>();
        if (!allChatIds.isEmpty()) {
            existingChatIds = StreamSupport.stream(chatRepository.findAllById(allChatIds).spliterator(), false)
                    .map(ChatEntity::getId)
                    .collect(Collectors.toSet());
        }

        List<Integer> folderIds = new ArrayList<>(folderInfoMap.keySet());

        Map<Integer, Set<Long>> existingChatIdsByFolder = new HashMap<>();
        if (!folderIds.isEmpty()) {
            existingChatIdsByFolder = folderChatRepository.findByFolder_IdIn(folderIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            link -> link.getFolder().getId(),
                            Collectors.mapping(
                                    link -> link.getChat().getId(),
                                    Collectors.toSet()
                            )
                    ));
        }

        List<FolderChatEntity> newLinks = new ArrayList<>();
        List<Integer> folderIdsToDelete = new ArrayList<>();

        for (Map.Entry<Integer, FolderInfo> entry : folderInfoMap.entrySet()) {
            Integer folderId = entry.getKey();
            FolderInfo folderInfo = entry.getValue();

            Set<Long> newChatIds = folderInfo.getChatIds() != null
                    ? folderInfo.getChatIds().stream()
                    .filter(existingChatIds::contains)
                    .collect(Collectors.toSet())
                    : new HashSet<>();

            Set<Long> existingIds = existingChatIdsByFolder.getOrDefault(folderId, Collections.emptySet());

            if (!newChatIds.equals(existingIds)) {
                folderIdsToDelete.add(folderId);

                for (Long chatId : newChatIds) {
                    newLinks.add(new FolderChatEntity(folderId, chatId));
                }
            }
        }

        if (!folderIdsToDelete.isEmpty()) {
            folderChatRepository.deleteByFolderIdIn(folderIdsToDelete);
        }

        if (!newLinks.isEmpty()) {
            folderChatRepository.saveAll(newLinks);
        }
    }

    private List <FolderEntity> buildFoldersToSave(
            List<FolderInfo> folders, Map<Integer,
            FolderEntity> existingFolders,
            Integer tgAccountId
    ) {
        List<FolderEntity> foldersToSave = new ArrayList<>();

        for (FolderInfo folderInfo : folders) {
            FolderEntity folderEntity = existingFolders.get(folderInfo.getId());

            if (folderEntity == null || !Objects.equals(folderInfo.getTitle(), folderEntity.getTitle())) {
                folderEntity = folderMapper.toEntity(folderInfo);
                folderEntity.setTgId(tgAccountId);
                folderEntity.setTarget(folderInfo.getTitle().equals(targetFolderTitle));
            } else {
                if (folderEntity.getTgId() == null) {
                    folderEntity.setTgId(tgAccountId);
                }
            }

            foldersToSave.add(folderEntity);
        }
        return foldersToSave;
    }
}