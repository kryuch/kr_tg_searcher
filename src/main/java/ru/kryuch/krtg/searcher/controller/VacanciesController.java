package ru.kryuch.krtg.searcher.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyFilter;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyListItemDto;
import ru.kryuch.krtg.searcher.service.VacancyService;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacanciesController extends BaseController {

    private final VacancyService vacancyService;

    @GetMapping({"", "/"})
    public String index(@ModelAttribute("vfilter") VacancyFilter vfilter,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {
        Page<VacancyListItemDto> vacancies = vacancyService.findPage(vfilter, page);
        int current = vacancies.getNumber() + 1;
        int total = vacancies.getTotalPages();

        model.addAttribute("vacancies", vacancies);
        model.addAttribute("currentPage", current);
        model.addAttribute("totalPages", total);
        model.addAttribute("pageFrom", Math.max(1, current - 3));
        model.addAttribute("pageTo", Math.min(total, current + 3));
        model.addAttribute("statuses", vacancyService.findStatuses());
        model.addAttribute("workFormats", WorkFormat.values());
        model.addAttribute("filterQuery", filterQuery(vfilter));
        return renderer("vacancy/list", model);
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("vacancy", vacancyService.getById(id));
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/vacancies/";
        }
        return renderer("vacancy/detail", model);
    }

    private String filterQuery(VacancyFilter filter) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        if (StringUtils.hasText(filter.getStatus())) {
            builder.queryParam("status", filter.getStatus());
        }
        if (filter.getWorkFormat() != null) {
            builder.queryParam("workFormat", filter.getWorkFormat());
        }
        if (filter.getCreatedFrom() != null) {
            builder.queryParam("createdFrom", filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            builder.queryParam("createdTo", filter.getCreatedTo());
        }
        String query = builder.build().encode().getQuery();
        return query == null ? "" : "&" + query;
    }
}