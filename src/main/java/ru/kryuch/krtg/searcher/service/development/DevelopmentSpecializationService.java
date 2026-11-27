package ru.kryuch.krtg.searcher.service.development;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSpecializationDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;
import ru.kryuch.krtg.searcher.mapper.development.DevelopmentSpecializationMapper;
import ru.kryuch.krtg.searcher.repository.development.DevelopmentSpecializationRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional
public class DevelopmentSpecializationService extends AbstractService <Long, DevelopmentSpecializationEntity, DevelopmentSpecializationDto, DevelopmentSpecializationMapper, DevelopmentSpecializationRepository> {


    public DevelopmentSpecializationService(final DevelopmentSpecializationRepository repository, final DevelopmentSpecializationMapper mapper) {
        super(repository, mapper, "DevelopmentSpecialization");
    }
}
