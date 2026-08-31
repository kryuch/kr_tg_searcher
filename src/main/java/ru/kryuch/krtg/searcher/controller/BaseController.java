package ru.kryuch.krtg.searcher.controller;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseController {

    protected String renderer(String templateName, Model model) {
        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

        model.addAttribute("page", templateName);
        return "index";
    }
}