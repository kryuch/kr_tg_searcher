package ru.kryuch.krtg.searcher.type;

import java.util.Arrays;

public enum ChatStatus {

    SIMPLE(0, "-"),
    REFUSE(1, "отказ"),
    EMPTY(2, "нет вакансий"),
    FAIL(3, "нет HR"),
    SEND_ERROR(4, "ощибка в отправке");

    final Integer type;
    final String value;

    ChatStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }

    public static ChatStatus getChatStatus(Integer type) {
        return Arrays.stream(ChatStatus.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }
}
