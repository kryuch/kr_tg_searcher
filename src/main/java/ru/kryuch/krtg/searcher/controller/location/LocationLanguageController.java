package ru.kryuch.krtg.searcher.controller.location;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.location.LocationLanguageService;

@Controller
@RequestMapping("/location/languages")
public class LocationLanguageController extends TBaseController<LocationLanguageService> {

    public LocationLanguageController(final LocationLanguageService service) {
        super(service, "location/language");
    }
}
