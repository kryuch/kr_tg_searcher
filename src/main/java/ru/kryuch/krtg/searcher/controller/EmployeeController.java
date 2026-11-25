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
import ru.kryuch.krtg.searcher.dto.OptionDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.dto.location.LocationStateDto;
import ru.kryuch.krtg.searcher.service.EmployeeService;
import ru.kryuch.krtg.searcher.service.GradeService;
import ru.kryuch.krtg.searcher.service.location.LocationCityService;
import ru.kryuch.krtg.searcher.service.location.LocationLanguageService;
import ru.kryuch.krtg.searcher.service.location.LocationStateService;

import java.util.Comparator;
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

    private static final String DEFAULT_COUNTRY_CODE = "RU";

    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<LocationStateDto> states = locationStateService.getAll();
        Long defaultStateId = states.stream()
            .filter(s -> DEFAULT_COUNTRY_CODE.equals(s.getCode()))
            .map(LocationStateDto::getId)
            .findFirst()
            .orElse(null);

        model.addAttribute("employee", EmployeeDto.builder()
            .citizenshipId(defaultStateId)
            .stateId(defaultStateId)
            .build());
        model.addAttribute("citizenships", states);
        model.addAttribute("states", states);
        return renderer(templateName + "/edit", model);
    }

    @GetMapping("/cities")
    @ResponseBody
    public List<OptionDto> cities(@RequestParam Long stateId) {
        return locationCityService.getByStateId(stateId).stream()
            .sorted(Comparator.comparing(LocationCityDto::getName, String.CASE_INSENSITIVE_ORDER))
            .map(c -> new OptionDto(c.getId(), c.getName()))
            .toList();
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
