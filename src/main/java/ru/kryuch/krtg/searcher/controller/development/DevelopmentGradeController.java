package ru.kryuch.krtg.searcher.controller.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.development.DevelopmentGradeService;

@Controller
@RequestMapping("/development/grades")
public class DevelopmentGradeController extends TBaseController<DevelopmentGradeService> {


    public DevelopmentGradeController(final DevelopmentGradeService gradeService) {
        super(gradeService, "development/grade");
    }


}
