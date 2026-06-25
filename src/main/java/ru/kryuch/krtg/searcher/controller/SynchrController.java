package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.service.ChatExportService;
import ru.kryuch.krtg.searcher.service.ChatService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/synchr")
public class SynchrController {

    private final ChatService chatService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "synchr");

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        return "index";
    }

    @PostMapping(value = "/action")
    public String action(RedirectAttributes redirectAttributes) {
        Boolean synchr = chatService.synchr();
        redirectAttributes.addFlashAttribute("successMessage", "Синхронизация выполнена");
        return "redirect:/synchr/";
    }

}
