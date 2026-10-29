package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;

import java.util.List;

/** То, что реально присылает клиент, отвечая на вопрос. */
@Data
public class VacancyQuestionAnswerRequestDto {

    private Integer questionId;
    private String textValue;
    private Boolean boolValue;
    private List<Integer> selectedOptionIds;
}