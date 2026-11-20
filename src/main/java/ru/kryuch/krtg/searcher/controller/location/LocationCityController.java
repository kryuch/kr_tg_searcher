package ru.kryuch.krtg.searcher.controller.location;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.service.location.LocationCityService;

@Controller
@RequestMapping("/location/cities")
public class LocationCityController extends TBaseController <LocationCityService> {

    public LocationCityController(final LocationCityService service) {
        super(service, "location/city");
    }
}
