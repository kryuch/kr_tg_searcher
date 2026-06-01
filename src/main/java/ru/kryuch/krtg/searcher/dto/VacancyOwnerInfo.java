package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

@Data
public class VacancyOwnerInfo {

    // 1 - телефон, 2 - email, 3 - телеграмм, 4 - другое
    Integer type;

    String value;
}
