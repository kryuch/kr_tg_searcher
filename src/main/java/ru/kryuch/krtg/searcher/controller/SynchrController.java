package ru.kryuch.krtg.searcher.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.helper.ExportHelper;
import ru.kryuch.krtg.searcher.service.ChatBugfixService;
import ru.kryuch.krtg.searcher.service.ChatExportService;
import ru.kryuch.krtg.searcher.service.ChatSynchronizationService;
import ru.kryuch.krtg.searcher.service.TgAccountService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/synchr")
public class SynchrController {

    private final ExportHelper exportHelper;
    private final ChatExportService chatExportService;
    private final ChatBugfixService chatBugfixService;
    private final TgAccountService tgAccountService;
    private final ChatSynchronizationService chatSynchronizationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "synchr");
        model.addAttribute("tgAccounts", tgAccountService.getAll());
        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        return "index";
    }

    @PostMapping(value = "/action")
    public String action(@RequestParam("tgAccountIds") List<Integer> tgAccountIds, RedirectAttributes redirectAttributes) {
        chatSynchronizationService.synchronize(tgAccountIds);
        redirectAttributes.addFlashAttribute("successMessage", "Синхронизация выполнена");
        return "redirect:/synchr/";
    }

    @PostMapping(value = "/export")
    public void export(@RequestParam("tgAccountIds") List<Integer> tgAccountIds, HttpServletResponse response) {
        exportHelper.export(chatExportService.exportByTgIds(tgAccountIds), response);
    }

    @PostMapping(value = "/bugfix")
    public String bugfix(@RequestParam("tgAccountIds") List<Integer> tgAccountIds, RedirectAttributes redirectAttributes) {
        chatBugfixService.action(tgAccountIds);
        redirectAttributes.addFlashAttribute("successMessage", "Исправление ошибок выполнен");
        return "redirect:/synchr/";
    }
}

