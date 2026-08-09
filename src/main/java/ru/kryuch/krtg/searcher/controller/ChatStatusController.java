package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.integration.ai.AiClient;
import ru.kryuch.krtg.searcher.integration.ai.AiClientFactory;
import ru.kryuch.krtg.searcher.service.ChatService;
import ru.kryuch.krtg.searcher.service.FolderChatService;

import java.util.List;

@RestController
@RequestMapping("/chat/status")
@RequiredArgsConstructor
public class ChatStatusController {

    private final ChatService chatServiceImpl;
    private final FolderChatService folderChatService;

    @PostMapping("/update")
    public Boolean update(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return chatServiceImpl.update(chatId, username, name, status);
    }

    @PostMapping("/folder")
    public Boolean folder(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return true;//folderChatService.updateLinksToTarget(List.of(chatId), status.equals(Integer.valueOf(1)));
    }

}