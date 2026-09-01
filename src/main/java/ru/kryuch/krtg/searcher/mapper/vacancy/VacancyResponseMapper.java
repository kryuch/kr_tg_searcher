package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyResponseMapper implements TMapper<VacancyResponseEntity, VacancyResponseDto> {


}