package ru.kryuch.krtg.searcher.service.setting;

import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.type.SettingType;

@Component
public class SettingValueConverter {

    public void apply(
            SettingValueEntity entity,
            SettingType type,
            String value
    ) {
        switch (type) {
            case BOOLEAN -> entity.setBoolValue(parseBoolean(value));
            case INTEGER -> entity.setIntValue(parseInteger(value));
            case DOUBLE -> entity.setDoubleValue(parseDouble(value));
            case STRING -> entity.setStringValue(value);
        }
    }

    private Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw new BusinessException(
                "Некорректное boolean значение: " + value
        );
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new BusinessException(
                    "Некорректное integer значение: " + value
            );
        }
    }

    private Double parseDouble(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new BusinessException(
                    "Некорректное double значение: " + value
            );
        }
    }
}