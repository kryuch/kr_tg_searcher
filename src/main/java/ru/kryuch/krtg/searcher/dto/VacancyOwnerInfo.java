package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.VacancyOwnerType;

@Data
public class VacancyOwnerInfo {

    private VacancyOwnerType type;
    private String value;
}
