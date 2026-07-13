package ru.kryuch.krtg.searcher.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.service.TgAccountService;

import java.util.Objects;

@Controller
@RequestMapping("/tg")
@RequiredArgsConstructor
public class TgAccountController {

    private final TgAccountService tgAccountService;

    @GetMapping("/list")
    public String getList(Model model) {
        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        }
        model.addAttribute("items", tgAccountService.getAll());
        model.addAttribute("page", "tg/list");
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("tg", new TgAccountInfo());
        model.addAttribute("page", "tg/add");
        return "index";
    }

    @GetMapping("/{tgId}/remove")
    public String remove(@PathVariable("tgId") Integer tgId, Model model) {
        model.addAttribute("tg", new TgAccountInfo());
        tgAccountService.remove(tgId);
        return "redirect:/tg/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("tg", tgAccountService.get(id));
        model.addAttribute("page", "tg/add");
        return "index";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid @ModelAttribute("tg") TgAccountInfo info, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("page", "tg/add");
            model.addAttribute("error", result.getAllErrors().toString());
            return "index";
        } else {
            tgAccountService.add(info);
            model.addAttribute("successMessage", String.format("ТГ-аккаунт %s успешно добавлен", info.getDescription()));
            return "redirect:/tg/list";
        }
    }
}
