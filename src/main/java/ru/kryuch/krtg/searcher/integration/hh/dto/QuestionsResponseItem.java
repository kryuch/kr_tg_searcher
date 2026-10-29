package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionsResponseItem {

    private QuestionsRequestItem value;

    private String answer;

    private boolean boolAnswer;

    private List<Integer> optionAnswers;
}
