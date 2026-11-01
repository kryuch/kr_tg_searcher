package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.vacancy.VacancyOwnerType;

@Data
public class VacancyOwnerInfo {

    private VacancyOwnerType type;
    private String value;
}
