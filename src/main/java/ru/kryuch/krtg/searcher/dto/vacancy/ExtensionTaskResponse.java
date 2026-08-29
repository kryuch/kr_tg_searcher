package ru.kryuch.krtg.searcher.dto.vacancy;

import ru.kryuch.krtg.searcher.type.ExtensionTaskStatus;
import ru.kryuch.krtg.searcher.type.ExtensionTaskType;

public record ExtensionTaskResponse(
        Long id,
        ExtensionTaskType type,
        ExtensionTaskStatus status,
        Long vacancyId,
        String vacancyExternalId,
        String vacancyTitle,
        String vacancyUrl
) {
}