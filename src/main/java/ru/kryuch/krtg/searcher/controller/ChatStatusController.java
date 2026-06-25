package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.service.ChatExportService;
import ru.kryuch.krtg.searcher.service.ChatService;

@RestController
@RequestMapping("/chat/status")
@RequiredArgsConstructor
public class ChatStatusController {

    private final ChatService chatService;

    @PostMapping("/update")
    public Boolean update(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return chatService.update(chatId, username, name, status);
    }

    @PostMapping("/synchr")
    public Boolean synchr() {
        return chatService.synchr();
    }
}