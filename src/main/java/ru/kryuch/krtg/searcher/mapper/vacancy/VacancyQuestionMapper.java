package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyQuestionMapper implements TMapper<VacancyQuestionEntity, VacancyQuestionDto> {
}
