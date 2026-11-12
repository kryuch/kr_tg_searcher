package ru.kryuch.krtg.searcher.resume_export.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SendResumeRequest(
        // Заглушки, до реализации отправки файлов в ТГ-бот микросервис
        @NotNull
        Integer fromTgId,
        @NotNull
        Integer toTgId,

        // Форма документа, в котором нужно отправить сообщение и само резюме
        @NotNull(message = "Format must not be null")
        String format,

        @NotNull(message = "Resume must not be null")
        @Valid
        ResumeDto resume
) {
}
