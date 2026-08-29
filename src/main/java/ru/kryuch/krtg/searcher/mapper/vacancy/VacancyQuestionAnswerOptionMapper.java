package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerOptionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionOptionEntity;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyQuestionAnswerOptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract VacancyQuestionAnswerOptionEntity toEntity(VacancyQuestionAnswerEntity answer, VacancyQuestionOptionEntity option);
}
