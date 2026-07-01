package ru.kryuch.krtg.searcher.type;

public enum VacancyTgOwnerStatus {

    EXIST(1, "имеется"),
    NEW(2, "новый");


    final Integer type;
    final String value;

    VacancyTgOwnerStatus(Integer type, String value) {
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
