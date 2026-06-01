package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.service.ChatService;

@RestController
@RequestMapping("/chat/status")
@RequiredArgsConstructor
public class ChatStatusController {

    private final ChatService chatService;

    @PostMapping("/update")
    public Boolean update(@Param("chatId") Long chatId, @Param("name") String name, Integer status) {
        return chatService.update(chatId, name, status);
    }

    @PostMapping("/synchr")
    public Boolean synchr() {
        return chatService.synchr();
    }

    @PostMapping("/{chatId}/vacancies/send")
    public Boolean vacancies(@PathVariable Long chatId,
                            @RequestParam(value = "term", required = false) String term,
                            @RequestParam(value = "text", required = false) String text,
                            Model model) {
        MessagesHistory messagesHistory = chatService.messages(chatId, 100);
        return chatService.sendToVacancyOwners(messagesHistory, term, text);
    }
}