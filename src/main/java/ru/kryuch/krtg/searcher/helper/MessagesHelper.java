package ru.kryuch.krtg.searcher.helper;

import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.dto.VacancyOwnerInfo;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MessagesHelper {

    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "(?i)(?:#вакансия\\s*)?(?:\\*\\*)?([А-Яа-я\\s\\-\\/]+(?:разработчик|инженер|devops|developer|admin|аналитик|менеджер|дизайнер|программист))",
            Pattern.MULTILINE
    );

    private static final Pattern TG_USERNAME_PATTERN = Pattern.compile("@(\\w+)");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?[\\d\\s\\-()]{10,}");

    private static final Pattern REMOTE_PATTERN = Pattern.compile(
            "(?i)(удалённ?|remote|дистанционн?|из дому|home|из дома)"
    );

    private static final Pattern OFFICE_PATTERN = Pattern.compile(
            "(?i)(офис|office|в офисе|не удалённо)"
    );

    public static VacancyInfo createVacancyInfo(String text, LocalDateTime dateTime) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        text = cleanText(text);
        String title = extractTitle(text);
        List<VacancyOwnerInfo> owners = extractOwners(text);
        Integer remoteStatus = determineRemoteStatus(text);

        return VacancyInfo.builder()
                .title(title)
                .text(text)
                .owners(owners)
                .dateTime(dateTime)
                .remoteStatus(remoteStatus)
                .build();
    }

    private static String cleanText(String text) {
        String[] noiseMarkers = {
                "**Java Job**",
                "Бесплатный постинг вакансий",
                "–––",
                "Забирай 📚 Базу Знаний",
                "🔜 А избранные IT-вакансии"
        };

        for (String marker : noiseMarkers) {
            int index = text.indexOf(marker);
            if (index > 0) {
                text = text.substring(0, index);
                break;
            }
        }

        return text.trim();
    }

    private static String extractTitle(String text) {
        Matcher matcher = TITLE_PATTERN.matcher(text);
        if (matcher.find()) {
            String title = matcher.group(1).trim();
            if (!title.isEmpty()) {
                return title;
            }
        }
        String firstLine = text.split("\n")[0];
        return firstLine.length() > 100 ? firstLine.substring(0, 100) : firstLine;
    }

    private static List<VacancyOwnerInfo> extractOwners(String text) {
        Map<String, VacancyOwnerInfo> ownersMap = new LinkedHashMap<>();

        // Markdown ссылки на Telegram
        Pattern markdownLinkPattern = Pattern.compile("\\[([^\\]]+)\\]\\((?:https?://)?t\\.me/(\\w+)\\)");
        Matcher markdownMatcher = markdownLinkPattern.matcher(text);
        while (markdownMatcher.find()) {
            String value = "@" + markdownMatcher.group(2);
            ownersMap.put(value, createOwnerInfo(3, value));
        }

        // Прямые ссылки на t.me
        Pattern directTgPattern = Pattern.compile("(?:https?://)?t\\.me/(\\w+)");
        Matcher directTgMatcher = directTgPattern.matcher(text);
        while (directTgMatcher.find()) {
            String value = "@" + directTgMatcher.group(1);
            ownersMap.put(value, createOwnerInfo(3, value));
        }

        // Telegram username
        Matcher tgMatcher = TG_USERNAME_PATTERN.matcher(text);
        while (tgMatcher.find()) {
            String value = "@" + tgMatcher.group(1);
            ownersMap.put(value, createOwnerInfo(3, value));
        }

        // Email
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        while (emailMatcher.find()) {
            String value = emailMatcher.group();
            ownersMap.put(value, createOwnerInfo(2, value));
        }

        // Телефон
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        while (phoneMatcher.find()) {
            String value = phoneMatcher.group().trim();
            ownersMap.put(value, createOwnerInfo(1, value));
        }

        return new ArrayList<>(ownersMap.values());
    }

    private static VacancyOwnerInfo createOwnerInfo(Integer type, String value) {
        VacancyOwnerInfo owner = new VacancyOwnerInfo();
        owner.setType(type);
        owner.setValue(value);
        return owner;
    }

    private static Integer determineRemoteStatus(String text) {
        boolean isRemote = REMOTE_PATTERN.matcher(text).find();
        boolean isOffice = OFFICE_PATTERN.matcher(text).find();

        if (isRemote && !isOffice) {
            return 1;
        } else if (!isRemote && isOffice) {
            return 2;
        } else {
            return 3;
        }
    }
}