package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.dto.TgUserAvatar;
import ru.kryuch.krtg.searcher.service.ChatService;
import ru.kryuch.krtg.searcher.service.FolderChatService;

import java.util.List;

@RestController
@RequestMapping("/chat/status")
@RequiredArgsConstructor
public class ChatStatusController {

    private final ChatService chatService;
    private final FolderChatService folderChatService;

    @PostMapping("/update")
    public Boolean update(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return chatService.update(chatId, username, name, status);
    }

    @PostMapping("/folder")
    public Boolean folder(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return true;//folderChatService.updateLinksToTarget(List.of(chatId), status.equals(Integer.valueOf(1)));
    }

    @PostMapping("/avatars")
    public List <TgUserAvatar> avatars(@RequestBody List <ChatKey> keys) {
        return chatService.getAvatars(keys);
    }


}
