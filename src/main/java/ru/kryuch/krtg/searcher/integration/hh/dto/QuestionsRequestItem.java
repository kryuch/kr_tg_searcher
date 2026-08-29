package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.QuestionType;

import java.util.List;

@Data
public class QuestionsRequestItem {

    private String text;

    private QuestionType type;

    private List<String> options;
}
