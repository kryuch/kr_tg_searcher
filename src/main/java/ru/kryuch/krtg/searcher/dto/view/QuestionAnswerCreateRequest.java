package ru.kryuch.krtg.searcher.dto.view;

import jakarta.validation.constraints.NotBlank;

public record QuestionAnswerCreateRequest(
        @NotBlank
        String question,

        @NotBlank
        String answer
) {
}