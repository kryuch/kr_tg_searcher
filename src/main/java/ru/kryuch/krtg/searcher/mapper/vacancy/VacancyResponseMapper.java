package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseRequestDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyResponseMapper implements TMapper<VacancyResponseEntity, VacancyResponseDto> {

    @Mapping(source = "vacancy.externalId", target = "externalId")
    @Mapping(source = "vacancy.url", target = "url")
    @Mapping(source = "vacancy.title", target = "title")
    @Mapping(source = "vacancy.description", target = "description")
    @Mapping(source = "vacancy.ownerOrganisation.name", target = "owner")
    public abstract  VacancyResponseDto fromEntity(VacancyResponseEntity entity);

    public abstract VacancyResponseEntity toEntity(VacancyResponseRequestDto vacancyResponseRequestDto);
}