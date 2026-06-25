package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.service.IgnoreService;
import jakarta.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/ignore")
@RequiredArgsConstructor
public class IgnoreController {

    private final IgnoreService ignoreService;

    @GetMapping("/list")
    public String getList(Model model) {
        model.addAttribute("items", ignoreService.getAll());
        model.addAttribute("page", "ignore/list");
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("ignore", new IgnoreInfo());
        model.addAttribute("page", "ignore/add");
        return "index";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid @ModelAttribute("ignore") IgnoreInfo info, BindingResult result, Model model) {
        if (result.hasErrors() || Objects.isNull(info.getUsername()) || info.getUsername().isEmpty()) {
            model.addAttribute("page", "ignore/add");
            return "index";
        } else {
            ignoreService.add(info);
            return "redirect:/ignore" +
                    "/list";
        }
    }
}
