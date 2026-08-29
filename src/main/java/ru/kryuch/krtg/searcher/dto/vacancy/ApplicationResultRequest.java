package ru.kryuch.krtg.searcher.dto.vacancy;

public record ApplicationResultRequest(
        boolean success,
        String errorMessage
) {
}