package ru.kryuch.krtg.searcher.type;

import java.util.Arrays;

public enum SearchLastMessageType {

    IGNORE(0, "не использовать"),
    ONLY(1, "только такое"),
    EXCLUDE(-1, "кроме");

    final Integer type;
    final String value;

    SearchLastMessageType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }

    public static SearchLastMessageType getSearchLastMessageType(Integer type) {
        return Arrays.stream(SearchLastMessageType.values())
                .filter(item -> item.getType().equals(type)).
                findFirst().orElse(null);
    }

}
