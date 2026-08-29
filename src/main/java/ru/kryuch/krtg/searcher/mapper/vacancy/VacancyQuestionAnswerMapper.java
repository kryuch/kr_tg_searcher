package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerOptionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = VacancyQuestionOptionMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyQuestionAnswerMapper implements TMapper<VacancyQuestionAnswerEntity, VacancyQuestionAnswerDto> {

    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "question.text", target = "questionText")
    @Mapping(source = "question.type", target = "questionType")
    @Mapping(source = "question.options", target = "options")
    @Mapping(source = "selectedOptions", target = "selectedOptionIds", qualifiedByName = "toOptionIds")
    public abstract VacancyQuestionAnswerDto fromEntity(VacancyQuestionAnswerEntity entity);

    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "text", target = "questionText")
    @Mapping(source = "type", target = "questionType")
    @Mapping(source = "options", target = "options")
    public abstract VacancyQuestionAnswerDto fromQuestion(VacancyQuestionEntity question);

    @Mapping(target = "question", source = "question")
    public abstract VacancyQuestionAnswerEntity toEntity(VacancyQuestionEntity question, Integer userId);

    @Named("toOptionIds")
    List<Integer> toOptionIds(List<VacancyQuestionAnswerOptionEntity> selected) {
        if (selected == null) return List.of();
        return selected.stream()
                .map(s -> s.getOption().getId())
                .toList();
    }

    @AfterMapping
    protected void afterToEntity(
            @MappingTarget VacancyQuestionAnswerEntity vacancyQuestionAnswerEntity,
            VacancyQuestionEntity question,
            Integer userId) {
        vacancyQuestionAnswerEntity.setUserId(userId);

    }
}
