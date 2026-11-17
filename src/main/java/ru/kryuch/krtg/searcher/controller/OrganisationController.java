package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.service.OrganisationService;

@Controller
@RequestMapping("/organisations")
@RequiredArgsConstructor
public class OrganisationController extends BaseController {

    private final OrganisationService organisationService;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", organisationService.getAll());
        return renderer("organisations/list", model);
    }
}
