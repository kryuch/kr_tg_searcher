package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderEntity;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final ChatRepository chatRepository;
    private final FolderChatRepository folderChatRepository;
    private final TelegramPythonClient telegramPythonClient;
    private final FolderMapper folderMapper;
    private final SettingService settingService;

    private final String SETTING_VALUE = "folder";

    @Transactional
    public void synchronize(Integer tgAccountId, boolean forceFlag) {
        String targetFolderTitle = settingService.getValueByCode(SETTING_VALUE);

        if (forceFlag) {
            folderRepository.deleteByTgId(tgAccountId);
        }

        List<FolderInfo> folders = telegramPythonClient.findAllFolders(tgAccountId);

        for (FolderInfo folderInfo : folders) {
            // 1. Сохраняем или обновляем папку
            FolderEntity folderEntity = folderRepository.findById(folderInfo.getId()).orElse(null);

            if (folderEntity == null || !folderInfo.getTitle().equals(folderEntity.getTitle())) {
                folderEntity = folderMapper.toEntity(folderInfo);
                folderEntity.setTgId(tgAccountId);
                folderEntity.setTarget(folderInfo.getTitle().equals(targetFolderTitle));
                folderEntity = folderRepository.save(folderEntity);
            }

            synchronizeFolderChats(folderInfo);
        }
    }

    private void synchronizeFolderChats(FolderInfo folderInfo) {
        // 2. Получаем текущие ID чатов в папке
        Set<Long> currentChatIds = folderChatRepository.findByFolder_Id(folderInfo.getId())
                .stream()
                .map(item -> item.getChat().getId())
                .collect(Collectors.toSet());

        // 3. Если чаты изменились, обновляем
        if (!currentChatIds.equals(folderInfo.getChatIds())) {
            // Удаляем старые связи
            folderChatRepository.deleteByFolderId(folderInfo.getId());

            // Добавляем новые связи
            if (folderInfo.getChatIds() != null && !folderInfo.getChatIds().isEmpty()) {
                // Сохраняем связи только с существующими чатами
                List<FolderChatEntity> newLinks = folderInfo.getChatIds().stream()
                        .filter(item -> chatRepository.existsById(item))
                        .map(chatId -> {
                            // Создаём ChatEntity только с ID (для связи)
                            ChatEntity chatEntity = new ChatEntity();
                            chatEntity.setId(chatId);
                            return new FolderChatEntity(folderInfo.getId(), chatId);
                        })
                        .collect(Collectors.toList());

                folderChatRepository.saveAll(newLinks);
            }
        }
    }
}