package ru.kryuch.krtg.searcher.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.education.EducationSpecialtyService;

@Controller
@RequestMapping("/education/speciality")
public class EducationSpecialityController extends TBaseController <EducationSpecialtyService> {

    public EducationSpecialityController(final EducationSpecialtyService service) {
        super(service, "education/speciality");
    }
}
