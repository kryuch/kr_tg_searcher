package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.SettingType;

import java.io.Serializable;

@Data
public class Setting implements Serializable {

    private String code;

    private String title;

    private String group;
    private String groupTitle;

    private SettingType type;

    private Boolean boolValue;

    private Integer intValue;

    private Double doubleValue;

    private String stringValue;

    /**
     * Метод для обратной совместимости с фронтом, который ожидает
     * единое строковое значение независимо от типа настройки.
     */
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