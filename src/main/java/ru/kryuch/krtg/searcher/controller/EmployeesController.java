package ru.kryuch.krtg.searcher.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import ru.kryuch.krtg.searcher.dto.BreadCrumb;
import ru.kryuch.krtg.searcher.entity.employee.DevelopmentDirectionEntity;
import ru.kryuch.krtg.searcher.repository.employee.DevelopmentDirectionRepository;
import ru.kryuch.krtg.searcher.service.MainMenuService;

@Controller
@RequestMapping("/employees")
public class EmployeesController {
    private final DevelopmentDirectionRepository developmentDirectionRepository;
    private final MainMenuService mainMenuService;
    private final HttpServletRequest httpServletRequest;

    public EmployeesController(
        DevelopmentDirectionRepository developmentDirectionRepository,
        MainMenuService mainMenuService,
        HttpServletRequest httpServletRequest
    ) {
        this.developmentDirectionRepository = developmentDirectionRepository;
        this.mainMenuService = mainMenuService;
        this.httpServletRequest = httpServletRequest;
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("title", "Сотрудники");
        model.addAttribute("page", "/employees/");
        model.addAttribute("pageTemplate", "employees/fragments-main");
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());
        model.addAttribute("breadcrumbs", List.of(new BreadCrumb("Сотрудники", "#")));

        return "employees/index";
    }

    @GetMapping("/directions/")
    public String getDirectionsPage(Model model) {
        model.addAttribute("title", "Направления разработки");
        model.addAttribute("page", "/employees/directions/");
        model.addAttribute("pageTemplate", "employees/fragments-directions");
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());
        model.addAttribute("developmentDirections", developmentDirectionRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "#")
        ));

        return "employees/index";
    }

    @DeleteMapping("/directions/{id}")
    public String deleteDevelopmentDirection(@PathVariable Long id) {
        developmentDirectionRepository.deleteById(id);
        
        return "redirect:/employees/directions/";
    }

    @GetMapping("/directions/{id}")
    public String getDevelopmentDirection(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Редактировать направление разработки");
        model.addAttribute("page", "/employees/directions/");
        model.addAttribute("pageTemplate", "employees/fragments-direction");
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());
        model.addAttribute("developmentDirection", developmentDirectionRepository.findById(id).get());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "/employees/directions/"),
            new BreadCrumb("Редактировать направление разработки", "#")
        ));

        return "employees/index";
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
        model.addAttribute("page", "/employees/directions/add/");
        model.addAttribute("pageTemplate", "employees/fragments-direction-add");
        model.addAttribute("menuItems", mainMenuService.getMenuItems());
        model.addAttribute("currentUri", httpServletRequest.getRequestURI());
        model.addAttribute("breadcrumbs", List.of(
            new BreadCrumb("Сотрудники", "/employees/"),
            new BreadCrumb("Направления разработки", "/employees/directions/"),
            new BreadCrumb("Добавить направление разработки", "#")
        ));

        return "employees/index";
    }

    @PostMapping("/directions/add")
    public String postDevelopmentDirectionAddPage(@RequestParam("name") String name, Model model) {
        DevelopmentDirectionEntity entity = new DevelopmentDirectionEntity(name);
        developmentDirectionRepository.save(entity);

        return "redirect:/employees/directions/";
    }
}
