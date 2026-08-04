package ru.kryuch.krtg.searcher.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kryuch.krtg.searcher.dto.CurrentUser;

import java.util.Set;
import java.util.stream.Collectors;

public class UserUtil {
    public static String normalizeUsername(String username) {
        return username.startsWith("@")
                ? username.substring(1)
                : username;
    }

    public static Set <String> normalizeUsernames(Set<String> usernames) {
        return usernames.stream().map(item -> UserUtil.normalizeUsername(item)).collect(Collectors.toSet());
    }

    public static boolean isAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return true;
    }

    public static CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CurrentUser) {
            return (CurrentUser) principal;
        }
        throw new IllegalStateException("Principal не является CurrentUser");
    }
}