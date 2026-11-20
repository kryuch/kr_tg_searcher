package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.OrganisationDto;
import ru.kryuch.krtg.searcher.entity.OrganisationEntity;
import ru.kryuch.krtg.searcher.mapper.OrganisationMapper;
import ru.kryuch.krtg.searcher.repository.OrganisationRepository;

@Service
public class OrganisationService extends AbstractService <Long, OrganisationEntity, OrganisationDto, OrganisationMapper, OrganisationRepository> {

    public OrganisationService(final OrganisationRepository repository, final OrganisationMapper mapper) {
        super(repository, mapper, "Organisation");
    }
}
