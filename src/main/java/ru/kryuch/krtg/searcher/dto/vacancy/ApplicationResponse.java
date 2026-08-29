package ru.kryuch.krtg.searcher.dto.vacancy;

import ru.kryuch.krtg.searcher.type.VacancyApplicationStatus;

import java.util.List;

public record ApplicationResponse(
        Long id,
        Long vacancyId,
        VacancyApplicationStatus status,
        List<VacancyQuestionDto> questions,
        String errorMessage
) {
}