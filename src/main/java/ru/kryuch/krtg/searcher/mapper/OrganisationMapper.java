package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.OrganisationDto;
import ru.kryuch.krtg.searcher.entity.OrganisationEntity;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class OrganisationMapper implements TMapper <OrganisationEntity, OrganisationDto> {
}
