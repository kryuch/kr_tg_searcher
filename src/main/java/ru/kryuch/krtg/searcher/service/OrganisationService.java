package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.OrganisationDto;
import ru.kryuch.krtg.searcher.mapper.OrganisationMapper;
import ru.kryuch.krtg.searcher.repository.OrganisationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final OrganisationMapper organisationMapper;

    public List<OrganisationDto> getAll() {
        return organisationMapper.fromEntityList(organisationRepository.findAll());
    }
}
