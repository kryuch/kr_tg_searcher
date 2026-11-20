package ru.kryuch.krtg.searcher.controller.location;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.location.LocationStateService;

@Controller
@RequestMapping("/location/states")
public class LocationStateController extends TBaseController <LocationStateService> {

    public LocationStateController(final LocationStateService service) {
        super(service, "location/state");
    }
}
