package ru.kryuch.krtg.searcher.type.vacancy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkFormat {
    ON_SITE("На территории клиента"),
    REMOTE("Удалённо"),
    HYBRID("Гибрид");

    private final String title;
}