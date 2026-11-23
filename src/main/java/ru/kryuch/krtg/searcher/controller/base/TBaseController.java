package ru.kryuch.krtg.searcher.controller.base;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Controller
@RequiredArgsConstructor
public abstract class TBaseController <SERVICE extends AbstractService> extends BaseController {

    protected final SERVICE service;
    protected final String templateName;

    @GetMapping("/")
    public String getList(Model model) {
        model.addAttribute("items", service.getAll());
        return renderer(templateName + "/list", model);
    }


}