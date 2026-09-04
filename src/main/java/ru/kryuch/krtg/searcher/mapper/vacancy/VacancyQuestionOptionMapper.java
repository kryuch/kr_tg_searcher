package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionOptionDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionOptionEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyQuestionOptionMapper implements TMapper<VacancyQuestionOptionEntity, VacancyQuestionOptionDto> {

    public abstract VacancyQuestionOptionEntity toEntity(VacancyQuestionOptionDto dto);

    public abstract VacancyQuestionOptionEntity mergeToEntity(VacancyQuestionOptionDto dto, @MappingTarget VacancyQuestionOptionEntity entity);
}