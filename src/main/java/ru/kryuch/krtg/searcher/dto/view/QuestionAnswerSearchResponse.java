package ru.kryuch.krtg.searcher.dto.view;

public record QuestionAnswerSearchResponse(
        boolean found,
        Long id,
        String answer
) {
}