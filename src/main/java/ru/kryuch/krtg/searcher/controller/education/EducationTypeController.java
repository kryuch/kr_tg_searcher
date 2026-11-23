package ru.kryuch.krtg.searcher.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.education.EducationTypeService;

@Controller
@RequestMapping("/education/types")
public class EducationTypeController extends TBaseController<EducationTypeService> {


    public EducationTypeController(final EducationTypeService service) {
        super(service, "education/type");
    }
}
