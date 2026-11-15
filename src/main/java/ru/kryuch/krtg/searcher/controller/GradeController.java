package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.service.GradeService;

@Controller
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController extends BaseController{

    private final GradeService gradeService;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", gradeService.getAll());
        return renderer("grade/list", model);
    }
}
