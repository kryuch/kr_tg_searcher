package ru.kryuch.krtg.searcher.integration.hh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseRequestDto;
import ru.kryuch.krtg.searcher.integration.hh.dto.VacancyResponseRequest;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyResponseRequestMapper {

    public abstract VacancyResponseRequestDto toDto(VacancyResponseRequest vacancyResponseRequest);

}