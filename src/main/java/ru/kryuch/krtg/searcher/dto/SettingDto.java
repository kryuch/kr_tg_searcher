package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.SettingType;

@Data
public class SettingDto {

    private String code;
    private String title;
    private String group;
    private String groupTitle;
    private SettingType type;
    private Boolean boolValue;
    private Integer intValue;
    private Double doubleValue;
    private String stringValue;
    private Boolean isLarge;

    public String getValue() {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case BOOLEAN -> boolValue == null ? null : boolValue.toString();
            case INTEGER -> intValue == null ? null : intValue.toString();
            case DOUBLE -> doubleValue == null ? null : doubleValue.toString();
            case STRING -> stringValue;
        };
    }
}