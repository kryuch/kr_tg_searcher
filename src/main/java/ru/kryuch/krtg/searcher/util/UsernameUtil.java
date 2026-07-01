package ru.kryuch.krtg.searcher.util;

public class UsernameUtil {
    public static String normalizeUsername(String username) {
        return username.startsWith("@")
                ? username.substring(1)
                : username;
    }
}