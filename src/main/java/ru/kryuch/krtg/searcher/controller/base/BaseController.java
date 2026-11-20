package ru.kryuch.krtg.searcher.controller.base;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.service.AbstractService;

public abstract class BaseController <SERVICE extends AbstractService> {

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