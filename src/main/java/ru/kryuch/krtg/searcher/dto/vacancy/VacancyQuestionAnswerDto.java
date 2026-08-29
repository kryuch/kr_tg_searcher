package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.QuestionType;

import java.util.List;

/** Для показа вопроса пользователю вместе с уже данным ответом (если есть). */
@Data
public class VacancyQuestionAnswerDto {

    private Integer id;
    private Integer questionId;
    private String questionText;
    private QuestionType questionType;
    private List<VacancyQuestionOptionDto> options;

    private String textValue;
    private Boolean boolValue;
    private List<Integer> selectedOptionIds;

    private boolean answered;
}