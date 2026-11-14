package ru.kryuch.krtg.searcher.type.vacancy;

public enum VacancySource {

    SKILLSTAFF("SKILLSTAFF", "skillstaf"),
    HH("HH", "hh");

    final String code;
    final String title;

    VacancySource(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

}
