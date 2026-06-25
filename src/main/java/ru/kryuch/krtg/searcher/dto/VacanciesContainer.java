package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class VacanciesContainer {

    MessagesHistory messagesHistory;

    List<VacancyInfo> vacancies;

    Set<String> newTg;
}
