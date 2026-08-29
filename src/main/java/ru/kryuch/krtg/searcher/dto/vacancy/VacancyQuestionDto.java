package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.QuestionType;

import java.util.List;

@Data
public class VacancyQuestionDto {

        private Integer id;
        private String text;
        private QuestionType type;
        private boolean required;
        private List<VacancyQuestionOptionDto> options;
}