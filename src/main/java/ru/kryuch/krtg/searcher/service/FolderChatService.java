package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.mapper.FolderMapper;
import ru.kryuch.krtg.searcher.repository.FolderChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderChatService {

    private final FolderChatRepository folderChatRepository;
    private final FolderMapper folderMapper;

    public List<FolderInfo> getFoldersByChatId(Long chatId) {
        return folderChatRepository.findByChat_Id(chatId).stream()
                .map(item -> item.getFolder())
                .map(item -> folderMapper.fromEntity(item))
                .toList();
    }
}
