package ru.kryuch.krtg.searcher.type;

public enum SendMessageStatus {

    SUCCESS(1, "отправлено"),
    SKIP(2, "пропущено"),
    ERROR(3, "ошибка");

    final Integer type;
    final String value;

    SendMessageStatus(Integer type, String value) {
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
