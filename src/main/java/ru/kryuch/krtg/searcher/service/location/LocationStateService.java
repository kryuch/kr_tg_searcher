package ru.kryuch.krtg.searcher.service.location;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.location.LocationStateDto;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.mapper.location.LocationStateMapper;
import ru.kryuch.krtg.searcher.repository.location.LocationStateRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LocationStateService extends AbstractService<Long, LocationStateEntity, LocationStateDto, LocationStateMapper, LocationStateRepository> {

    public LocationStateService(final LocationStateRepository repository, final LocationStateMapper mapper) {
        super(repository, mapper, "State");
    }
}
