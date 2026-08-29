package ru.kryuch.krtg.searcher.controller.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerFormDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.service.vacancy.VacancyQuestionAnswerService;
import ru.kryuch.krtg.searcher.type.QuestionType;

import java.util.List;

@Controller
@RequestMapping("/vacancy/questions/answers")
@RequiredArgsConstructor
public class VacancyQuestionAnswerController {

    private static final String LIST_PAGE = "vacancy/answer/list";
    private static final String FORM_PAGE = "vacancy/answer/edit";
    private static final String REDIRECT_LIST = "redirect:/vacancy/questions/answers/";

    private final VacancyQuestionAnswerService vacancyQuestionAnswerService;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", vacancyQuestionAnswerService.getAllWithAnswers());
        model.addAttribute("page", LIST_PAGE);
        return "index";
    }

    @GetMapping("/{questionId}/edit")
    public String editForm(@PathVariable Integer questionId, Model model) {
        VacancyQuestionAnswerDto answer = vacancyQuestionAnswerService.getByQuestionId(questionId);

        model.addAttribute("answer", answer);
        model.addAttribute("form", toFormDto(answer));
        model.addAttribute("page", FORM_PAGE);
        return "index";
    }

    @PostMapping("/{questionId}/edit")
    public String save(@PathVariable Integer questionId,
                       @ModelAttribute("form") VacancyQuestionAnswerFormDto form) {

        VacancyQuestionAnswerRequestDto request = new VacancyQuestionAnswerRequestDto();
        request.setQuestionId(questionId);
        request.setTextValue(form.getTextValue());
        request.setBoolValue(form.getBoolValue());
        request.setSelectedOptionIds(resolveSelectedOptionIds(form));

        vacancyQuestionAnswerService.saveAnswer(request);
        return REDIRECT_LIST;
    }

    private VacancyQuestionAnswerFormDto toFormDto(VacancyQuestionAnswerDto answer) {
        VacancyQuestionAnswerFormDto form = new VacancyQuestionAnswerFormDto();
        form.setQuestionId(answer.getQuestionId());
        form.setTextValue(answer.getTextValue());
        form.setBoolValue(answer.getBoolValue());

        List<Integer> selected = answer.getSelectedOptionIds();
        if (answer.getQuestionType() == QuestionType.SINGLE_OPTION) {
            form.setSingleOptionId(selected == null || selected.isEmpty() ? null : selected.get(0));
        } else {
            form.setSelectedOptionIds(selected);
        }
        return form;
    }

    private List<Integer> resolveSelectedOptionIds(VacancyQuestionAnswerFormDto form) {
        if (form.getSingleOptionId() != null) {
            return List.of(form.getSingleOptionId());
        }
        return form.getSelectedOptionIds();
    }
}