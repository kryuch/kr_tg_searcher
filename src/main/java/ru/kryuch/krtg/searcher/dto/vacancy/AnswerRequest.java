package ru.kryuch.krtg.searcher.dto.vacancy;

public record AnswerRequest(
        Long questionId,
        String value
) {
}