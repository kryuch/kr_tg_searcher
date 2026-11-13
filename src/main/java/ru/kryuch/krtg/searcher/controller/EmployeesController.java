package ru.kryuch.krtg.searcher.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
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
import org.stringtemplate.v4.compiler.CodeGenerator.primary_return;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.BreadCrumb;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeDto;
import ru.kryuch.krtg.searcher.entity.employee.DevelopmentDirectionEntity;
import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.GradeEntity;
import ru.kryuch.krtg.searcher.entity.employee.OrganizationEntity;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;
import ru.kryuch.krtg.searcher.entity.employee.SpecialtyEntity;
import ru.kryuch.krtg.searcher.entity.employee.UniversityEntity;
import ru.kryuch.krtg.searcher.repository.employee.DevelopmentDirectionRepository;
import ru.kryuch.krtg.searcher.repository.employee.EducationTypeRepository;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeRepository;
import ru.kryuch.krtg.searcher.repository.employee.GradeRepository;
import ru.kryuch.krtg.searcher.repository.employee.OrganizationRepository;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeProfileRepository;
import ru.kryuch.krtg.searcher.repository.employee.SkillRepository;
import ru.kryuch.krtg.searcher.repository.employee.SpecialtyRepository;
import ru.kryuch.krtg.searcher.repository.employee.UniversityRepository;
import ru.kryuch.krtg.searcher.service.MainMenuService;

import ru.kryuch.krtg.searcher.service.employee.*;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeesController {
    private final EmployeeService employeeService;
    private final EmployeeProfileService employeeProfileService;
    private final GradeService gradeService;
    private final SkillService skillService;
    private final OrganizationService organizationService;
    private final UniversityService universityService;
    private final SpecialtyService specialtyService;
    private final EducationTypeService educationTypeService;
    private final EmployeeDevelopmentDirectionService developmentDirectionService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final DevelopmentDirectionRepository developmentDirectionRepository;
    private final GradeRepository gradeRepository;
    private final SkillRepository skillRepository;
    private final OrganizationRepository organizationRepository;
    private final UniversityRepository universityRepository;
    private final SpecialtyRepository specialtyRepository;
    private final EducationTypeRepository educationTypeRepository;

    private final HttpServletRequest httpServletRequest;
    private final MainMenuService mainMenuService;

    public String common(String template, Model model) {
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());

        return template;
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("title", "Сотрудники");
        model.addAttribute("pageTemplate", "employee");
        model.addAttribute("items", employeeRepository.findAllByOrderByIdAsc());
        model.addAttribute("breadcrumbs", List.of(new BreadCrumb("Сотрудники", "#")));

        return common("employees/index", model);
    }

    @GetMapping("/add/")
    public String getEmployeeAddPage(Model model) {
        model.addAttribute("title", "Добавить сотрудника");
        model.addAttribute("pageTemplate", "employee-add");
        model.addAttribute("form", new EmployeeDto());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Добавить сотрудника", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/add/")
    public String postEmployeeAddPage(@ModelAttribute EmployeeDto employeeDto) {
        EmployeeEntity employeeEntity = new EmployeeEntity();

        employeeEntity.setLastName(employeeDto.getLastName());
        employeeEntity.setFirstName(employeeDto.getFirstName());
        employeeEntity.setMiddleName(employeeDto.getMiddleName());
        employeeEntity.setGender(employeeDto.getGender());
        employeeEntity.setBirthDate(employeeDto.getBirthDate());
        employeeEntity.setCitizenship("");

        employeeRepository.save(employeeEntity);

        return "redirect:/employees/";
    }

    @DeleteMapping("/{id}/")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);

        return "redirect:/employees/";
    }

    @GetMapping("/{id}/")
    public String getEmployeeEditPage(@PathVariable("id") Long id, Model model) {
        EmployeeEntity entity = employeeRepository.findById(id).get();

        EmployeeDto form = new EmployeeDto();
        form.setLastName(entity.getLastName());
        form.setFirstName(entity.getFirstName());
        form.setMiddleName(entity.getMiddleName());
        form.setBirthDate(entity.getBirthDate());
        form.setGender(entity.getGender());
        System.out.println("Gender: " + form.getGender());

        model.addAttribute("title", "Редактировать сотрудника");
        model.addAttribute("pageTemplate", "employee-edit");
        model.addAttribute("id", id);
        model.addAttribute("form", form);
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Редактировать сотрудника", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/{id}/")
    public String postEmployeeEditPage(@ModelAttribute EmployeeDto form, @PathVariable("id") Long id) {
        EmployeeEntity entity = employeeRepository.findById(id).get();

        entity.setLastName(form.getLastName());
        entity.setFirstName(form.getFirstName());
        entity.setMiddleName(form.getMiddleName());
        entity.setGender(form.getGender());
        entity.setBirthDate(form.getBirthDate());

        employeeRepository.save(entity);
        
        return "redirect:/employees/";
    }

    @GetMapping("/profiles/")
    public String getProfilesPage(Model model) {
        model.addAttribute("title", "Профили");
        model.addAttribute("pageTemplate", "profile");
        model.addAttribute("items", employeeProfileRepository.findAllByOrderByIdAscWithDetails());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Профили", "#"))
        );

        return common("employees/index", model);
    }

    @GetMapping("/directions/")
    public String getDirectionsPage(Model model) {
        model.addAttribute("title", "Направления разработки");
        model.addAttribute("pageTemplate", "direction");
        model.addAttribute("developmentDirections", developmentDirectionRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "#")
        ));

        return common("employees/index", model);
    }

    @DeleteMapping("/directions/{id}")
    public String deleteDevelopmentDirection(@PathVariable Long id) {
        developmentDirectionRepository.deleteById(id);

        return "redirect:/employees/directions/";
    }

    @GetMapping("/directions/{id}")
    public String getDevelopmentDirection(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать направление разработки");
        model.addAttribute("pageTemplate", "direction-edit");
        model.addAttribute("developmentDirection", developmentDirectionRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "/employees/directions/"),
            new BreadCrumb("Редактировать направление разработки", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/directions/{id}")
    public String patchDevelopmentDirection(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        DevelopmentDirectionEntity entity = developmentDirectionRepository.findById(id).get();
        entity.setName(newName);
        developmentDirectionRepository.save(entity);

        return "redirect:/employees/directions/";
    }

    @GetMapping("/directions/add/")
    public String getDevelopmentDirectionAddPage(Model model) {
        model.addAttribute("title", "Добавить направление разработки");
        model.addAttribute("pageTemplate", "direction-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "/employees/directions/"),
            new BreadCrumb("Добавить направление разработки", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/directions/add")
    public String postDevelopmentDirectionAddPage(@RequestParam("name") String name, Model model) {
        DevelopmentDirectionEntity entity = new DevelopmentDirectionEntity(name);
        developmentDirectionRepository.save(entity);

        return "redirect:/employees/directions/";
    }

    /************** Grades ************************/

    @GetMapping("/grades/")
    public String getGradesPage(Model model) {
        model.addAttribute("title", "Уровни разработчика");
        model.addAttribute("pageTemplate", "grade");
        model.addAttribute("items", gradeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Уровни разработчика", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/grades/add/")
    public String getGradesAddPage(Model model) {
        model.addAttribute("title", "Добавить уровень разработчика");
        model.addAttribute("pageTemplate", "grade-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Уровни разработчика", "/employees/directions/"),
            new BreadCrumb("Добавить уровень разработчика", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/grades/add/")
    public String postGradesAddPage(@RequestParam("name") String name, Model model) {
        GradeEntity entity = new GradeEntity(name);
        gradeRepository.save(entity);

        return "redirect:/employees/grades/";
    }

    @GetMapping("/grades/{id}/")
    public String getGradesEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать уровень разработчика");
        model.addAttribute("pageTemplate", "grade-edit");
        model.addAttribute("item", gradeRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Уровни разработчика", "/employees/grades/"),
            new BreadCrumb("Редактировать уровни разработчика", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/grades/{id}/")
    public String patchGradesEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        GradeEntity entity = gradeRepository.findById(id).get();
        entity.setName(newName);
        gradeRepository.save(entity);

        return "redirect:/employees/grades/";
    }

    @DeleteMapping("/grades/{id}/")
    public String deleteGradesEditPage(@PathVariable Long id) {
        gradeRepository.deleteById(id);

        return "redirect:/employees/grades/";
    }

    /************** Skills ************************/

    @GetMapping("/skills/")
    public String getSkillsPage(Model model) {
        model.addAttribute("title", "Навыки и технологии");
        model.addAttribute("pageTemplate", "skill");
        model.addAttribute("items", skillRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Навыки и технологии", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/skills/add/")
    public String getSkillsAddPage(Model model) {
        model.addAttribute("title", "Добавить навык и технологию");
        model.addAttribute("pageTemplate", "skill-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Уровни разработчика", "/employees/skills/"),
            new BreadCrumb("Добавить навык и технологию", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/skills/add/")
    public String postSkillsAddPage(@RequestParam("name") String name, Model model) {
        SkillEntity entity = new SkillEntity(name);
        skillRepository.save(entity);

        return "redirect:/employees/skills/";
    }

    @GetMapping("/skills/{id}/")
    public String getSkillsEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать навык и технологию");
        model.addAttribute("pageTemplate", "skill-edit");
        model.addAttribute("item", skillRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Навыки и технологии", "/employees/skills/"),
            new BreadCrumb("Редактировать навыки и технологии", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/skills/{id}/")
    public String patchSkillsEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        SkillEntity entity = skillRepository.findById(id).get();
        entity.setName(newName);
        skillRepository.save(entity);

        return "redirect:/employees/skills/";
    }

    @DeleteMapping("/skills/{id}/")
    public String deleteSkillsEditPage(@PathVariable Long id) {
        skillRepository.deleteById(id);

        return "redirect:/employees/skills/";
    }

    /************** Organizations ************************/

    @GetMapping("/organizations/")
    public String getOrganizationsPage(Model model) {
        model.addAttribute("title", "Компании, в которых работал");
        model.addAttribute("pageTemplate", "organization");
        model.addAttribute("items", organizationRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Компании, в которых работал", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/organizations/add/")
    public String getOrganizationsAddPage(Model model) {
        model.addAttribute("title", "Добавить компанию, в которой работал");
        model.addAttribute("pageTemplate", "organization-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Компании, в которых работал", "/employees/organizations/"),
            new BreadCrumb("Добавить компанию, в которой работал", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/organizations/add/")
    public String postOrganizationsAddPage(@RequestParam("name") String name, Model model) {
        OrganizationEntity entity = new OrganizationEntity(name);
        organizationRepository.save(entity);

        return "redirect:/employees/organizations/";
    }

    @GetMapping("/organizations/{id}/")
    public String getOrganizationsEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать компанию, в которой работал");
        model.addAttribute("pageTemplate", "organization-edit");
        model.addAttribute("item", organizationRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Компании, в которых работал", "/employees/organizations/"),
            new BreadCrumb("Редактировать компанию, в которых работал", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/organizations/{id}/")
    public String patchOrganizationsEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        OrganizationEntity entity = organizationRepository.findById(id).get();
        entity.setName(newName);
        organizationRepository.save(entity);

        return "redirect:/employees/organizations/";
    }

    @DeleteMapping("/organizations/{id}/")
    public String deleteOrganizationsEditPage(@PathVariable Long id) {
        organizationRepository.deleteById(id);

        return "redirect:/employees/organizations/";
    }

    /************** Universities ************************/

    @GetMapping("/universities/")
    public String getUniversitiesPage(Model model) {
        model.addAttribute("title", "Университеты");
        model.addAttribute("pageTemplate", "university");
        model.addAttribute("items", universityRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Университеты", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/universities/add/")
    public String getUniversitiesAddPage(Model model) {
        model.addAttribute("title", "Добавить университет");
        model.addAttribute("pageTemplate", "university-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Университеты", "/employees/universities/"),
            new BreadCrumb("Добавить университет", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/universities/add/")
    public String postUniversitiesAddPage(@RequestParam("name") String name, Model model) {
        UniversityEntity entity = new UniversityEntity(name);
        universityRepository.save(entity);

        return "redirect:/employees/universities/";
    }

    @GetMapping("/universities/{id}/")
    public String getUniversitiesEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать университет");
        model.addAttribute("pageTemplate", "university-edit");
        model.addAttribute("item", universityRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Университеты", "/employees/universities/"),
            new BreadCrumb("Редактировать университет", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/universities/{id}/")
    public String patchUniversitiesEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        UniversityEntity entity = universityRepository.findById(id).get();
        entity.setName(newName);
        universityRepository.save(entity);

        return "redirect:/employees/universities/";
    }

    @DeleteMapping("/universities/{id}/")
    public String deleteUniversitiesEditPage(@PathVariable Long id) {
        universityRepository.deleteById(id);

        return "redirect:/employees/universities/";
    }

    /************** Specialties ************************/

    @GetMapping("/specialties/")
    public String getSpecialtiesPage(Model model) {
        model.addAttribute("title", "Специальности");
        model.addAttribute("pageTemplate", "specialty");
        model.addAttribute("items", specialtyRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Специальности", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/specialties/add/")
    public String getSpecialtiesAddPage(Model model) {
        model.addAttribute("title", "Добавить cпециальность");
        model.addAttribute("pageTemplate", "specialty-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Специальности", "/employees/specialties/"),
            new BreadCrumb("Добавить cпециальность", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/specialties/add/")
    public String postSpecialtiesAddPage(@RequestParam("name") String name, Model model) {
        SpecialtyEntity entity = new SpecialtyEntity(name);
        specialtyRepository.save(entity);

        return "redirect:/employees/specialties/";
    }

    @GetMapping("/specialties/{id}/")
    public String getSpecialtiesEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать cпециальность");
        model.addAttribute("pageTemplate", "specialty-edit");
        model.addAttribute("item", specialtyRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Специальности", "/employees/specialties/"),
            new BreadCrumb("Редактировать cпециальность", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/specialties/{id}/")
    public String patchSpecialtiesEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        SpecialtyEntity entity = specialtyRepository.findById(id).get();
        entity.setName(newName);
        specialtyRepository.save(entity);

        return "redirect:/employees/specialties/";
    }

    @DeleteMapping("/specialties/{id}/")
    public String deleteSpecialtiesEditPage(@PathVariable Long id) {
        specialtyRepository.deleteById(id);

        return "redirect:/employees/specialties/";
    }

    /************** EducationType ************************/

    @GetMapping("/educationtypes/")
    public String getEducationTypePage(Model model) {
        model.addAttribute("title", "Типы образования");
        model.addAttribute("pageTemplate", "educationtype");
        model.addAttribute("items", educationTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Типы образования", "#")
        ));

        return common("employees/index", model);
    }

    @GetMapping("/educationtypes/add/")
    public String getEducationTypeAddPage(Model model) {
        model.addAttribute("title", "Добавить тип образования");
        model.addAttribute("pageTemplate", "educationtype-add");
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Типы образования", "/employees/educationtypes/"),
            new BreadCrumb("Добавить тип образования", "#")
        ));

        return common("employees/index", model);
    }

    @PostMapping("/educationtypes/add/")
    public String postEducationTypeAddPage(@RequestParam("name") String name, Model model) {
        EducationTypeEntity entity = new EducationTypeEntity(name);
        educationTypeRepository.save(entity);

        return "redirect:/employees/educationtypes/";
    }

    @GetMapping("/educationtypes/{id}/")
    public String getEducationTypeEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать тип образования");
        model.addAttribute("pageTemplate", "educationtype-edit");
        model.addAttribute("item", educationTypeRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Типы образования", "/employees/educationtypes/"),
            new BreadCrumb("Редактировать тип образования", "#")
        ));

        return common("employees/index", model);
    }

    @PatchMapping("/educationtypes/{id}/")
    public String patchEducationTypeEditPage(@PathVariable Long id, @RequestParam("name") String newName, Model model) {
        EducationTypeEntity entity = educationTypeRepository.findById(id).get();
        entity.setName(newName);
        educationTypeRepository.save(entity);

        return "redirect:/employees/educationtypes/";
    }

    @DeleteMapping("/educationtypes/{id}/")
    public String deleteEducationTypeEditPage(@PathVariable Long id) {
        educationTypeRepository.deleteById(id);

        return "redirect:/employees/educationtypes/";
    }
}
