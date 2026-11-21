package ru.kryuch.krtg.searcher.service.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.mapper.location.LocationCityMapper;
import ru.kryuch.krtg.searcher.repository.location.LocationCityRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LocationCityService extends AbstractService <Long, LocationCityEntity, LocationCityDto, LocationCityMapper, LocationCityRepository> {

    public LocationCityService(final LocationCityRepository repository, final LocationCityMapper mapper) {
        super(repository, mapper, "City");
    }
}
