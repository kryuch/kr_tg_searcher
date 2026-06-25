package ru.kryuch.krtg.searcher.type;

public enum PersonalChatType {

    PERSONAL(1, "личный"),
    NOT_PERSONAL(2, "не личный"),
    ALL(3, "все");

    final Integer type;
    final String value;

    PersonalChatType(Integer type, String value) {
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
