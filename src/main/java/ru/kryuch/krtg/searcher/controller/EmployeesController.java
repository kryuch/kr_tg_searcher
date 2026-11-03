package ru.kryuch.krtg.searcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
public class EmployeesController {

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("title", "Сотрудники");
        model.addAttribute("page", "/employees/");
        model.addAttribute("pageTemplate", "employees-main");

        return "employees/index";
    }

    @GetMapping("/directions/")
    public String getDirectionsPage(Model model) {
        model.addAttribute("title", "Сотрудники");
        model.addAttribute("page", "/employees/directions/");
        model.addAttribute("pageTemplate", "employees-directions");
        
        return "employees/index";
    }

}
