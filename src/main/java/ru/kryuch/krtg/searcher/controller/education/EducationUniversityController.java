package ru.kryuch.krtg.searcher.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.education.EducationUniversityService;

@Controller
@RequestMapping("/education/universities")
public class EducationUniversityController extends TBaseController <EducationUniversityService> {

    public EducationUniversityController(final EducationUniversityService service) {
        super(service, "education/university");
    }
}
