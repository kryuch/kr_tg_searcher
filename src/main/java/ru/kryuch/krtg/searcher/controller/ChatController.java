package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.service.ChatService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("items", chatService.all());
        model.addAttribute("filter", new SearchParams());
        model.addAttribute("page", "chat/list");
        return "index";
    }

    @GetMapping("/search")
    public String search(SearchParams searchParams, Model model) {
        model.addAttribute("items", chatService.search(searchParams));
        return "chat/list";
    }

    @GetMapping("/{chatId}/messages")
    public String messages(@PathVariable("chatId") Long chatId, Model model) {
        model.addAttribute("messages", chatService.messages(chatId, 10));
        return "chat/messages";
    }

    @GetMapping("/{chatId}")
    public String chat(@PathVariable("chatId") Long chatId, Model model) {
        MessagesHistory messagesHistory = chatService.messages(chatId, 100);
        model.addAttribute("messages", messagesHistory);
        model.addAttribute("vacancies", chatService.vacancies(messagesHistory));
        model.addAttribute("id", chatId);
        model.addAttribute("page", "chat/one");
        return "index";
    }


}
