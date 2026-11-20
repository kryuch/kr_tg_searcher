package ru.kryuch.krtg.searcher.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.BreadCrumb;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.OrganizationDto;
import ru.kryuch.krtg.searcher.dto.employee.SkillDto;
import ru.kryuch.krtg.searcher.dto.employee.SpecialtyDto;
import ru.kryuch.krtg.searcher.dto.employee.UniversityDto;
import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.dto.employee.EducationTypeDto;
import ru.kryuch.krtg.searcher.service.EmployeeService;
import ru.kryuch.krtg.searcher.service.MainMenuService;

import ru.kryuch.krtg.searcher.service.employee.*;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController extends BaseController {
    private final EmployeeService employeeService;
    private final EmployeeProfileService employeeProfileService;
    private final SkillService skillService;
    private final OrganizationService organizationService;
    private final UniversityService universityService;
    private final SpecialtyService specialtyService;
    private final EducationTypeService educationTypeService;
    private final EmployeeDevelopmentDirectionService developmentDirectionService;
    private final HttpServletRequest httpServletRequest;
    private final MainMenuService mainMenuService;

    public String render(String template, Model model) {
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());

        return template;
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("title", "Сотрудники");
        model.addAttribute("pageTemplate", "employee");
        model.addAttribute("items", employeeService.getAll());
        model.addAttribute("breadcrumbs", List.of(new BreadCrumb("Сотрудники", "#")));

        return render("employee/employee", model);
    }

    @GetMapping("/add/")
    public String getEmployeeAddPage(Model model) {
        model.addAttribute("title", "Добавить сотрудника");
        model.addAttribute("pageTemplate", "employee-add");
        model.addAttribute("form", new EmployeeDto());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Добавить сотрудника", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/add/")
    public String postEmployeeAddPage(@ModelAttribute EmployeeDto employeeDto) {
        employeeService.save(employeeDto);

        return "redirect:/employee/";
    }

    @DeleteMapping("/{id}/")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);

        return "redirect:/employee/";
    }

    @GetMapping("/{id}/")
    public String getEmployeeEditPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("title", "Редактировать сотрудника");
        model.addAttribute("pageTemplate", "employee-edit");
        model.addAttribute("id", id);
        model.addAttribute("form", employeeService.get(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Редактировать сотрудника", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/{id}/")
    public String postEmployeeEditPage(@ModelAttribute EmployeeDto form, @PathVariable("id") Long id) {
        employeeService.save(form);
        
        return "redirect:/employee/";
    }

    @GetMapping("/profiles/")
    public String getProfilesPage(Model model) {
        model.addAttribute("title", "Профили");
        model.addAttribute("pageTemplate", "profile");
        model.addAttribute("items", employeeProfileService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Профили", "#"))
        );

        return render("employee/employee", model);
    }

    @GetMapping("/directions/")
    public String getDirectionsPage(Model model) {
        model.addAttribute("title", "Направления разработки");
        model.addAttribute("pageTemplate", "direction");
        model.addAttribute("developmentDirections", developmentDirectionService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Направления разработки", "#")
        ));

        return render("employee/employee", model);
    }

    @DeleteMapping("/directions/{id}")
    public String deleteDevelopmentDirection(@PathVariable Long id) {
        developmentDirectionService.deleteById(id);

        return "redirect:/employee/directions/";
    }

    @GetMapping("/directions/{id}")
    public String getDevelopmentDirection(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать направление разработки");
        model.addAttribute("pageTemplate", "direction-edit");
        model.addAttribute("developmentDirection", developmentDirectionService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Направления разработки", "/employee/directions/"),
            new BreadCrumb("Редактировать направление разработки", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/directions/{id}")
    public String patchDevelopmentDirection(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        DevelopmentDirectionDto entity = developmentDirectionService.findById(id);
        entity.setName(newName);
        developmentDirectionService.save(entity);

        return "redirect:/employee/directions/";
    }

    @GetMapping("/directions/add/")
    public String getDevelopmentDirectionAddPage(Model model) {
        model.addAttribute("title", "Добавить направление разработки");
        model.addAttribute("pageTemplate", "direction-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Направления разработки", "/employee/directions/"),
            new BreadCrumb("Добавить направление разработки", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/directions/add")
    public String postDevelopmentDirectionAddPage(@RequestParam("name") String name, Model model) {
        DevelopmentDirectionDto entity = new DevelopmentDirectionDto(name);
        developmentDirectionService.save(entity);

        return "redirect:/employee/directions/";
    }

    /************** Skills ************************/

    @GetMapping("/skills/")
    public String getSkillsPage(Model model) {
        model.addAttribute("title", "Навыки и технологии");
        model.addAttribute("pageTemplate", "skill");
        model.addAttribute("items", skillService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Навыки и технологии", "#")
        ));

        return render("employee/employee", model);
    }

    @GetMapping("/skills/add/")
    public String getSkillsAddPage(Model model) {
        model.addAttribute("title", "Добавить навык и технологию");
        model.addAttribute("pageTemplate", "skill-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Уровни разработчика", "/employee/skills/"),
            new BreadCrumb("Добавить навык и технологию", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/skills/add/")
    public String postSkillsAddPage(@RequestParam("name") String name, Model model) {
        skillService.save(new SkillDto(name));

        return "redirect:/employee/skills/";
    }

    @GetMapping("/skills/{id}/")
    public String getSkillsEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать навык и технологию");
        model.addAttribute("pageTemplate", "skill-edit");
        model.addAttribute("item", skillService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Навыки и технологии", "/employee/skills/"),
            new BreadCrumb("Редактировать навыки и технологии", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/skills/{id}/")
    public String patchSkillsEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        skillService.save(new SkillDto(id, newName));

        return "redirect:/employee/skills/";
    }

    @DeleteMapping("/skills/{id}/")
    public String deleteSkillsEditPage(@PathVariable Long id) {
        skillService.deleteById(id);

        return "redirect:/employee/skills/";
    }

    /************** Organizations ************************/

    @GetMapping("/organizations/")
    public String getOrganizationsPage(Model model) {
        model.addAttribute("title", "Компании, в которых работал");
        model.addAttribute("pageTemplate", "organization");
        model.addAttribute("items", organizationService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Компании, в которых работал", "#")
        ));

        return render("employee/employee", model);
    }

    @GetMapping("/organizations/add/")
    public String getOrganizationsAddPage(Model model) {
        model.addAttribute("title", "Добавить компанию, в которой работал");
        model.addAttribute("pageTemplate", "organization-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Компании, в которых работал", "/employee/organizations/"),
            new BreadCrumb("Добавить компанию, в которой работал", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/organizations/add/")
    public String postOrganizationsAddPage(@RequestParam("name") String name, Model model) {
        organizationService.save(new OrganizationDto(name));

        return "redirect:/employee/organizations/";
    }

    @GetMapping("/organizations/{id}/")
    public String getOrganizationsEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать компанию, в которой работал");
        model.addAttribute("pageTemplate", "organization-edit");
        model.addAttribute("item", organizationService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Компании, в которых работал", "/employee/organizations/"),
            new BreadCrumb("Редактировать компанию, в которых работал", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/organizations/{id}/")
    public String patchOrganizationsEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        organizationService.save(new OrganizationDto(id, newName));

        return "redirect:/employee/organizations/";
    }

    @DeleteMapping("/organizations/{id}/")
    public String deleteOrganizationsEditPage(@PathVariable Long id) {
        organizationService.deleteById(id);

        return "redirect:/employee/organizations/";
    }

    /************** Universities ************************/

    @GetMapping("/universities/")
    public String getUniversitiesPage(Model model) {
        model.addAttribute("title", "Университеты");
        model.addAttribute("pageTemplate", "university");
        model.addAttribute("items", universityService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Университеты", "#")
        ));

        return render("employee/employee", model);
    }

    @GetMapping("/universities/add/")
    public String getUniversitiesAddPage(Model model) {
        model.addAttribute("title", "Добавить университет");
        model.addAttribute("pageTemplate", "university-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Университеты", "/employee/universities/"),
            new BreadCrumb("Добавить университет", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/universities/add/")
    public String postUniversitiesAddPage(@RequestParam("name") String name, Model model) {
        universityService.save(new UniversityDto(name));

        return "redirect:/employee/universities/";
    }

    @GetMapping("/universities/{id}/")
    public String getUniversitiesEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать университет");
        model.addAttribute("pageTemplate", "university-edit");
        model.addAttribute("item", universityService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Университеты", "/employee/universities/"),
            new BreadCrumb("Редактировать университет", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/universities/{id}/")
    public String patchUniversitiesEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        universityService.save(new UniversityDto(id, newName));

        return "redirect:/employee/universities/";
    }

    @DeleteMapping("/universities/{id}/")
    public String deleteUniversitiesEditPage(@PathVariable Long id) {
        universityService.deleteById(id);

        return "redirect:/employee/universities/";
    }

    /************** Specialties ************************/

    @GetMapping("/specialties/")
    public String getSpecialtiesPage(Model model) {
        model.addAttribute("title", "Специальности");
        model.addAttribute("pageTemplate", "specialty");
        model.addAttribute("items", specialtyService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Специальности", "#")
        ));

        return render("employee/employee", model);
    }

    @GetMapping("/specialties/add/")
    public String getSpecialtiesAddPage(Model model) {
        model.addAttribute("title", "Добавить cпециальность");
        model.addAttribute("pageTemplate", "specialty-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Специальности", "/employee/specialties/"),
            new BreadCrumb("Добавить cпециальность", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/specialties/add/")
    public String postSpecialtiesAddPage(@RequestParam("name") String name, Model model) {
        specialtyService.save(new SpecialtyDto(name));

        return "redirect:/employee/specialties/";
    }

    @GetMapping("/specialties/{id}/")
    public String getSpecialtiesEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать cпециальность");
        model.addAttribute("pageTemplate", "specialty-edit");
        model.addAttribute("item", specialtyService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Специальности", "/employee/specialties/"),
            new BreadCrumb("Редактировать cпециальность", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/specialties/{id}/")
    public String patchSpecialtiesEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        specialtyService.save(new SpecialtyDto(id, newName));

        return "redirect:/employee/specialties/";
    }

    @DeleteMapping("/specialties/{id}/")
    public String deleteSpecialtiesEditPage(@PathVariable Long id) {
        specialtyService.deleteById(id);

        return "redirect:/employee/specialties/";
    }

    /************** EducationType ************************/

    @GetMapping("/educationtypes/")
    public String getEducationTypePage(Model model) {
        model.addAttribute("title", "Типы образования");
        model.addAttribute("pageTemplate", "educationtype");
        model.addAttribute("items", educationTypeService.findAll());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Типы образования", "#")
        ));

        return render("employee/employee", model);
    }

    @GetMapping("/educationtypes/add/")
    public String getEducationTypeAddPage(Model model) {
        model.addAttribute("title", "Добавить тип образования");
        model.addAttribute("pageTemplate", "educationtype-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Типы образования", "/employee/educationtypes/"),
            new BreadCrumb("Добавить тип образования", "#")
        ));

        return render("employee/employee", model);
    }

    @PostMapping("/educationtypes/add/")
    public String postEducationTypeAddPage(@RequestParam("name") String name, Model model) {
        educationTypeService.save(new EducationTypeDto(name));

        return "redirect:/employee/educationtypes/";
    }

    @GetMapping("/educationtypes/{id}/")
    public String getEducationTypeEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать тип образования");
        model.addAttribute("pageTemplate", "educationtype-edit");
        model.addAttribute("item", educationTypeService.findById(id));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employee/"),
            new BreadCrumb("Типы образования", "/employee/educationtypes/"),
            new BreadCrumb("Редактировать тип образования", "#")
        ));

        return render("employee/employee", model);
    }

    @PatchMapping("/educationtypes/{id}/")
    public String patchEducationTypeEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        educationTypeService.save(new EducationTypeDto(id, newName));

        return "redirect:/employee/educationtypes/";
    }

    @DeleteMapping("/educationtypes/{id}/")
    public String deleteEducationTypeEditPage(@PathVariable Long id) {
        educationTypeService.deleteById(id);

        return "redirect:/employee/educationtypes/";
    }
}
