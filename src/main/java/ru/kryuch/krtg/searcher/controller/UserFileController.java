package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.dto.UserFileDto;
import ru.kryuch.krtg.searcher.service.UserFileAccessService;

@Controller
@RequestMapping("/user/files")
@Slf4j
@RequiredArgsConstructor
public class UserFileController {

    private final UserFileAccessService userFileAccessService;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", userFileAccessService.getAll());
        model.addAttribute("page", "files/list");
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("ignore", new IgnoreInfo());
        model.addAttribute("page", "files/add");
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            userFileAccessService.upload(file);
        } catch (Exception ex) {
            log.error(String.valueOf(ex));
        }

        return "redirect:/user/files/";
    }
}
