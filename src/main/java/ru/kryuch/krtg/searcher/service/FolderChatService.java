package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.entity.FolderChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderEntity;
import ru.kryuch.krtg.searcher.integration.dto.FolderChatIdsRequestItem;
import ru.kryuch.krtg.searcher.integration.dto.UpdateFolderRequest;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;
import ru.kryuch.krtg.searcher.repository.FolderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderChatService {

    private final FolderChatRepository folderChatRepository;
    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;
    private final SettingService settingService;
    private final TelegramPythonClient telegramPythonClient;


//    private final String TARGET_FOLDER_TITLE = "folder";

    public List<FolderInfo> getFoldersByChatId(Long chatId) {
        return folderChatRepository.findByChat_Id(chatId).stream()
                .map(item -> item.getFolder())
                .map(item -> folderMapper.fromEntity(item))
                .toList();
    }

    public boolean addLinksToTarget(List <Long> chatIds, Boolean status) {
        chatIds.forEach(item -> this.addLinkToTarget(item, status));
        return true;
    }

    public boolean addLinkToTarget(Long chatId, Boolean status) {
        FolderEntity folderEntity = folderRepository.findAllByTarget(true).get(0);
        Boolean isExists = folderChatRepository.existsByFolder_IdAndChat_Id(folderEntity.getId(), chatId);

        if (status != isExists) {
            FolderChatEntity folderChatEntity = new FolderChatEntity(folderEntity.getId(), chatId);

            if (status) {
                folderChatRepository.save(folderChatEntity);
            }
            else {
                folderChatRepository.delete(folderChatEntity);
            }

            telegramPythonClient.updateFolder(
                    UpdateFolderRequest.builder()
                            .items(
                                    List.of(
                                            FolderChatIdsRequestItem.builder()
                                                    .folderId(folderEntity.getId())
                                                    .id(chatId)
                                                    .tgAccountId(folderEntity.getTgId())
                                                    .build()
                                    )
                            )
                            .addOperationFlag(status)
                            .build()
            );
        }
        return true;
    }
}
