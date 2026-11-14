package ru.kryuch.krtg.searcher.exception;

public class VacancyAlreadyExistsException extends RuntimeException {

    public VacancyAlreadyExistsException(String externalId) {
        super("Вакансия уже добавлена, externalId=" + externalId);
    }
}