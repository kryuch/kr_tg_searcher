package ru.kryuch.krtg.searcher.service.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.location.LocationLanguageDto;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.mapper.location.LocationLanguageMapper;
import ru.kryuch.krtg.searcher.repository.location.LocationLanguageRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LocationLanguageService extends AbstractService <Long, LocationLanguageEntity, LocationLanguageDto, LocationLanguageMapper, LocationLanguageRepository> {

    public LocationLanguageService(final LocationLanguageRepository repository, final LocationLanguageMapper mapper) {
        super(repository, mapper, "Language");
    }
}
