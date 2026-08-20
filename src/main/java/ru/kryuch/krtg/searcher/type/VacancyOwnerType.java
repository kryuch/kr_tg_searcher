package ru.kryuch.krtg.searcher.type;

public enum VacancyOwnerType {

    PHONE(1, "телефон"),
    EMAIL(2, "email"),
    TG(3, "тг"),
    OTHER(4, "другое");


    final Integer type;
    final String value;

    VacancyOwnerType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }
}
