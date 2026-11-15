package ru.kryuch.krtg.searcher.type;

import ru.kryuch.krtg.searcher.exception.UnsupportedFormatException;

import java.util.Locale;

public enum DocumentFormat {
    PDF, DOCX;

    public static DocumentFormat from(String value) {
        if (value == null) {
            throw new UnsupportedFormatException(null);
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedFormatException(value);
        }
    }

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }
}
