package ru.kryuch.krtg.searcher.dto.view;

import lombok.Data;

@Data
public class QuestionAnswerDto {
    private Long id;
    private String question;
    private String value;
    private boolean active;
}