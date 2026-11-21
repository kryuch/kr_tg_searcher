package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.ContactType;

@Data
public class VacancyOwnerInfo {

    private ContactType type;
    private String value;
}
