package ru.kryuch.krtg.searcher.controller.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionDto;
import ru.kryuch.krtg.searcher.service.vacancy.VacancyQuestionService;
import ru.kryuch.krtg.searcher.type.QuestionType;

@Controller
@RequestMapping("/vacancy/questions")
@RequiredArgsConstructor
public class VacancyQuestionController {

    private static final String LIST_PAGE = "vacancy/question/list";
    private static final String FORM_PAGE = "vacancy/question/create";
    private static final String REDIRECT_LIST = "redirect:/vacancy/questions/";

    private final VacancyQuestionService vacancyQuestionService;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", vacancyQuestionService.getAll());
        model.addAttribute("page", LIST_PAGE);
        return "index";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("question", new VacancyQuestionDto());
        model.addAttribute("questionTypes", QuestionType.values());
        model.addAttribute("page", FORM_PAGE);
        return "index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("question") VacancyQuestionDto question) {
        vacancyQuestionService.add(question);
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("question", vacancyQuestionService.getById(id));
        model.addAttribute("questionTypes", QuestionType.values());
        model.addAttribute("page", FORM_PAGE);
        return "index";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id,
                         @ModelAttribute("question") VacancyQuestionDto question) {
        vacancyQuestionService.update(question, id);
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/remove")
    public String remove(@PathVariable Integer id) {
        vacancyQuestionService.remove(id);
        return REDIRECT_LIST;
    }
}