package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.entity.FolderEntity;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.FolderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final TelegramPythonClient telegramPythonClient;
    private final FolderMapper folderMapper;

    private final SettingService settingService;


    private final String SETTING_VALUE = "folder";

    @Transactional
    public void synchronize(boolean forceFlag) {
        String tagetFolderTitle = settingService.getValueByCode(SETTING_VALUE);

        if (forceFlag) {
            folderRepository.deleteAll();
        }

        List<FolderInfo> folders = telegramPythonClient.findAllFolders();
        folders.forEach(folderInfo -> {
            FolderEntity folderEntity =
                    folderRepository.findById(folderInfo.getId()).orElse(null);

            if (folderEntity == null || !folderInfo.getTitle().equals(folderEntity.getTitle())) {
                folderEntity = folderMapper.toEntity(folderInfo);
                folderEntity.setIsTarget(folderInfo.getTitle().equals(tagetFolderTitle));
                folderRepository.save(folderEntity);
            }
        });
    }
}
