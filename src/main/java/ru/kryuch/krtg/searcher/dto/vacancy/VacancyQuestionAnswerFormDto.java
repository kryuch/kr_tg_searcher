package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;

import java.util.List;

/** Модель для формы редактирования ответа — разделяет single/multiple choice для корректных radio/checkbox. */
@Data
public class VacancyQuestionAnswerFormDto {

    private Integer questionId;
    private String textValue;
    private Boolean boolValue;
    private Integer singleOptionId;
    private List<Integer> selectedOptionIds;
}