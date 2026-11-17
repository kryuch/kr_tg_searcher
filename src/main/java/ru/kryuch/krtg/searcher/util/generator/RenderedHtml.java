package ru.kryuch.krtg.searcher.util.generator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Сущность, формирующаяся в результате рендеринга документа (например, резюме) в HTML
public record RenderedHtml(
        @NotNull(message = "Content must not be null")
        @NotBlank(message = "Content must not be blank")
        String content) {
}
