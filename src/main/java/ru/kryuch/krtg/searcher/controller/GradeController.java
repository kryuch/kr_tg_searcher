package ru.kryuch.krtg.searcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.BaseController;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.GradeService;

@Controller
@RequestMapping("/grades")
public class GradeController extends TBaseController<GradeService> {


    public GradeController(final GradeService gradeService) {
        super(gradeService, "grade");
    }


}
