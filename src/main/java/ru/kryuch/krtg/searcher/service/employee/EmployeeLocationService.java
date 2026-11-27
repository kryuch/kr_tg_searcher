package ru.kryuch.krtg.searcher.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.dto.location.LocationLanguageDto;
import ru.kryuch.krtg.searcher.dto.location.LocationStateDto;
import ru.kryuch.krtg.searcher.service.location.LocationCityService;
import ru.kryuch.krtg.searcher.service.location.LocationLanguageService;
import ru.kryuch.krtg.searcher.service.location.LocationStateService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeLocationService {

    private final LocationLanguageService languageService;
    private final LocationStateService stateService;

    public List<LocationLanguageDto> getLangs() {
        return languageService.getAll();
    }

    public List <LocationStateDto> getStates() {
        return stateService.getAll();
    }
}
