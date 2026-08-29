package ru.kryuch.krtg.searcher.dto.vacancy;

public record ExtensionTaskCompleteRequest(
        boolean success,
        String errorMessage
) {
}