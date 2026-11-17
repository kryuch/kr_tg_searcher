package ru.kryuch.krtg.searcher.type;

import java.util.Arrays;
import java.util.List;

public enum ChatStatus {

    REFUSE(1, "отказ"),
    NO_VACANCY(2, "нет вакансий"),
    NOT_HR(3, "не HR"),
    OTHER(4, "прочее"),
    PAUSE(5, "пока отложим");

    final Integer type;
    final String title;

    ChatStatus(Integer type, String value) {
        this.type = type;
        this.title = value;
    }

    public String getTitle() {
        return title;
    }

    public Integer getType() {
        return type;
    }

    public static ChatStatus getChatStatus(Integer type) {
        return Arrays.stream(ChatStatus.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    public static List <Integer> getExcludeList(List<Integer> types) {
        return Arrays.stream(ChatStatus.values())
            .filter(item -> !types.contains(item.getType()))
            .map(item -> item.getType())
            .toList();
    }
}
