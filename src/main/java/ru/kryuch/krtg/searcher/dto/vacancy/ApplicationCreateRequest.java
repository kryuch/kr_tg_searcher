package ru.kryuch.krtg.searcher.dto.vacancy;

public record ApplicationCreateRequest(
        String externalVacancyId,
        String title,
        String url,
        String description
) {
}