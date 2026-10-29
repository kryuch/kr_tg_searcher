package ru.kryuch.krtg.searcher.dto.view;

import jakarta.validation.constraints.NotBlank;

public record QuestionAnswerSearchRequest(
        @NotBlank
        String question
) {
}