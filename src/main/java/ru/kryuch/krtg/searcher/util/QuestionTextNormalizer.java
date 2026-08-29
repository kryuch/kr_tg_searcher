package ru.kryuch.krtg.searcher.util;

import java.util.regex.Pattern;

public final class QuestionTextNormalizer {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^\\p{L}\\p{Nd}]");

    private QuestionTextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return NON_ALPHANUMERIC.matcher(text.toLowerCase()).replaceAll("");
    }
}