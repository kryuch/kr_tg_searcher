package ru.kryuch.krtg.searcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.BaseController;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.OrganisationService;

@Controller
@RequestMapping("/organisations")
public class OrganisationController extends TBaseController<OrganisationService> {


    public OrganisationController(final OrganisationService service) {
        super(service, "organisations");
    }
}
