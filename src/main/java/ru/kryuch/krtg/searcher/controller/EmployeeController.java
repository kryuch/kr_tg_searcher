package ru.kryuch.krtg.searcher.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.service.EmployeeService;
import ru.kryuch.krtg.searcher.service.GradeService;
import ru.kryuch.krtg.searcher.service.location.LocationCityService;
import ru.kryuch.krtg.searcher.service.location.LocationLanguageService;
import ru.kryuch.krtg.searcher.service.location.LocationStateService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/employees")
public class EmployeeController extends TBaseController<EmployeeService> {

    private final LocationStateService locationStateService;
    private final LocationCityService locationCityService;
    private final LocationLanguageService locationLanguageService;
    private final GradeService gradeService;

    public EmployeeController(final EmployeeService service, final LocationStateService locationStateService, final LocationCityService locationCityService, final LocationLanguageService locationLanguageService, final GradeService gradeService) {
        super(service, "employee");
        this.locationStateService = locationStateService;
        this.locationCityService = locationCityService;
        this.locationLanguageService = locationLanguageService;
        this.gradeService = gradeService;
    }

    @GetMapping("/cities")
    @ResponseBody
    public List<String> cities(@RequestParam Long stateId) {
        return locationCityService.getByStateId(stateId).stream()
            .map(LocationCityDto::getName)
            .toList();
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", EmployeeDto.builder()
            .citizenship("RU")
            .state("RU")
            .build());
        model.addAttribute("citizenships", locationStateService.getAll());
        model.addAttribute("states", locationStateService.getAll());
        model.addAttribute("languages", locationLanguageService.getAll());
        model.addAttribute("grades", gradeService.getAll());
        return renderer(templateName + "/edit", model);
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("employee") EmployeeDto employee,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Некорректные данные в форме: " + bindingResult.getFieldError().getField());
            return "redirect:/employees/add";
        }

        try {
            removeEmptyItems(employee);
            EmployeeDto saved = service.save(employee);
            log.info("EmployeeController::save saved employee id={}", saved.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Сотрудник сохранён");
            return "redirect:/employees/";
        } catch (Exception e) {
            log.error("Не удалось сохранить сотрудника", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось сохранить: " + e.getMessage());
            return "redirect:/employees/add";
        }
    }

    /** Форма всегда шлёт пустые контакты, профиль и проект по умолчанию, поэтому незаполненное убираем. */
    private void removeEmptyItems(EmployeeDto employee) {
        if (employee.getContacts() != null) {
            employee.getContacts().removeIf(c -> c == null || isBlank(c.getValue()));
        }
        List<EmployeeProfileDto> profiles = employee.getProfiles();
        if (profiles != null) {
            profiles.forEach(p -> {
                if (p != null && p.getProjects() != null) {
                    p.getProjects().removeIf(pr -> pr == null || isBlank(pr.getName()));
                }
            });
            profiles.removeIf(p -> p == null || isBlank(p.getName()));
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
