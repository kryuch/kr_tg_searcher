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
import ru.kryuch.krtg.searcher.dto.location.LocationStateDto;
import ru.kryuch.krtg.searcher.service.EmployeeService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/employees")
public class EmployeeController extends TBaseController<EmployeeService> {

    public EmployeeController(final EmployeeService service) {
        super(service, "employee");
    }

    private static final String DEFAULT_COUNTRY_CODE = "RU";

    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<LocationStateDto> states = ((EmployeeService)service).getStates();
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
        model.addAttribute("universities", ((EmployeeService)service).getUniversities());
        model.addAttribute("specializations", ((EmployeeService)service).getSpecialities());
        model.addAttribute("educationDegree", ((EmployeeService)service).getEducationDegree());
        model.addAttribute("educationTypes", ((EmployeeService)service).getEducationTypes());
        model.addAttribute("specializations", ((EmployeeService)service).getDevelopmentSpecializations());
        model.addAttribute("skills", ((EmployeeService)service).getDevelopmentSkills());
        model.addAttribute("grades", ((EmployeeService)service).getDevelopmentGrades());
        model.addAttribute("languages", ((EmployeeService)service).getLanguages());
        return renderer(templateName + "/edit", model);
    }

    @GetMapping("/cities")
    @ResponseBody
    public List<OptionDto> cities(@RequestParam Long stateId) {
        return null;/*locationCityService.getByStateId(stateId).stream()
            .sorted(Comparator.comparing(LocationCityDto::getName, String.CASE_INSENSITIVE_ORDER))
            .map(c -> new OptionDto(c.getId(), c.getName()))
            .toList();*/
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
