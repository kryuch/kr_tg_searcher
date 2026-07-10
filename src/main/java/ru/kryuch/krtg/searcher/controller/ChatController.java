package ru.kryuch.krtg.searcher.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.SendMessageParam;
import ru.kryuch.krtg.searcher.dto.VacanciesContainer;
import ru.kryuch.krtg.searcher.service.ChatExportService;
import ru.kryuch.krtg.searcher.service.ChatService;
import ru.kryuch.krtg.searcher.service.FolderChatService;
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.service.TelegramMessagingService;
import ru.kryuch.krtg.searcher.service.TgAccountService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {
    private final ChatExportService chatExportService;
    private final ChatService chatService;
    private final FolderChatService folderChatService;
    private final SettingService settingService;
    private final TelegramMessagingService telegramMessagingService;
    private final TgAccountService tgAccountService;


    @GetMapping("/")
    public String list(Model model) {
        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        model.addAttribute("items", new ArrayList());
        model.addAttribute("filter", new SearchParams());
        model.addAttribute("tgAccounts", tgAccountService.getAll());
        model.addAttribute("page", "chat/list");
        return "index";
    }

    @GetMapping("/search")
    public String search(SearchParams searchParams, Model model) {
        model.addAttribute("items", chatService.search(searchParams, true));
        model.addAttribute("targetFolder", settingService.getValueByCode("folder"));
        model.addAttribute("tgAccounts", tgAccountService.getAll());
        return "chat/list";
    }

    @GetMapping("/{chatId}/messages")
    public String messages(@PathVariable("chatId") Long chatId, Model model) {
        model.addAttribute("messages", chatService.messages(chatId, 10));
        return "chat/messages";
    }

    @GetMapping("/{chatId}")
    public String chat(@PathVariable("chatId") Long chatId, Model model) {
        VacanciesContainer vacanciesContainer = chatService.createVacanciesContainer(chatId, 100);
        model.addAttribute("messages", vacanciesContainer.getMessagesHistory());
        model.addAttribute("data", vacanciesContainer);
        model.addAttribute("id", chatId);
        model.addAttribute("message", settingService.getValueByCode("first_message"));
        model.addAttribute("tgAccounts", tgAccountService.getAll());
        model.addAttribute("page", "chat/one");
        return "index";
    }

    @PostMapping("/send")
    public String sendMessage(
            SendMessageParam request,
            RedirectAttributes redirectAttributes) {

        List<ChatInfo> chats;

        if (Objects.nonNull(request.getBack())) {

            VacanciesContainer vacanciesContainer =
                    chatService.createVacanciesContainer(request.getBack(), 100);

            chats = telegramMessagingService.registerAndSend(
                    request,
                    vacanciesContainer.getNewTg()
            );

        } else {

            chats = telegramMessagingService.sendToChats(
                    request.getMessage(),
                    request.getChatIds()
            );
        }

        String successMessage = "Сообщение отправлено в " +
                chats.stream()
                        .map(ChatInfo::getName)
                        .collect(Collectors.joining(", "));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                successMessage
        );

        return request.getBack() != null
                ? "redirect:/chat/" + request.getBack()
                : "redirect:/chat/";
    }

    @PostMapping("/toFolder")
    public String toFolder(
            SendMessageParam request,
            RedirectAttributes redirectAttributes) {

        folderChatService.addLinksToTarget(request.getChatIds(), true);
        String successMessage = "Чаты добавлены в папку";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                successMessage
        );

        return "redirect:/chat/";
    }

    @PostMapping("/fromFolder")
    public String fromFolder(
            SendMessageParam request,
            RedirectAttributes redirectAttributes) {

        folderChatService.addLinksToTarget(request.getChatIds(), false);
        String successMessage = "Чаты добавлены в папку";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                successMessage
        );

        return "redirect:/chat/";
    }

    @PostMapping("/export")
    public void exportChats(@RequestParam("chatIds") List<Long> chatIds, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"exported_chats.txt\"");

            PrintWriter writer = response.getWriter();
            writer.write(chatExportService.export(chatIds));
            writer.flush();

        } catch (Exception e) {
            log.error("Ошибка при экспорте чатов", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Ошибка при экспорте: " + e.getMessage());
            } catch (IOException ex) {
                log.error("Ошибка записи ошибки", ex);
            }
        }
    }
}
