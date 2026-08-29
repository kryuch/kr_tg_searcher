package ru.kryuch.krtg.searcher.dto.vacancy;

import java.util.List;

public record AnswersUpdateRequest(
        List<AnswerRequest> answers
) {
}