package ru.kryuch.krtg.searcher.helper;

import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.dto.VacancyOwnerInfo;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MessagesHelper {

    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "(?i)(?:#вакансия\\s*)?(?:\\*\\*)?([А-Яа-я\\s\\-\\/]+(?:разработчик|инженер|devops|developer|admin|аналитик|менеджер|дизайнер|программиスト))",
            Pattern.MULTILINE
    );

    private static final Pattern TG_USERNAME_PATTERN = Pattern.compile(
            "@(?!gmail\\.com|mail\\.ru|yandex\\.ru|inbox\\.ru|list\\.ru|bk\\.ru|rambler\\.ru)(\\w+)"
    );
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?[\\d\\s\\-()]{10,}");

    private static final Pattern REMOTE_PATTERN = Pattern.compile(
            "(?i)(удалённ?|remote|дистанционн?|из дому|home|из дома)"
    );

    private static final Pattern OFFICE_PATTERN = Pattern.compile(
            "(?i)(офис|office|в офисе|не удалённо)"
    );

    // Список известных каналов и ботов, которые не являются личными контактами
    private static final Set<String> IGNORED_USERNAMES = new HashSet<>(Arrays.asList(
            "proglib_jobs", "job_javadevs", "freeIT_job", "easy_java_job",
            "Java_Job", "IT_Job", "devops_jobs", "python_jobs"
    ));

    public static VacancyInfo createVacancyInfo(String text, LocalDateTime dateTime) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        if (text.toLowerCase().contains("#cv") || text.toLowerCase().contains("#резюме")) return null;

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
                "🔜 А избранные IT-вакансии",
                "IT Job Hub"
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

    private static boolean isValidTgContact(String username) {
        if (username == null || username.isEmpty()) return false;
        // Исключаем известные каналы
        if (IGNORED_USERNAMES.contains(username)) {
            return false;
        }
        // Исключаем слишком короткие username
        if (username.length() < 4) {
            return false;
        }
        return true;
    }

    private static List<VacancyOwnerInfo> extractOwners(String text) {
        Map<String, VacancyOwnerInfo> ownersMap = new LinkedHashMap<>();
        Set<String> emails = new HashSet<>();

        // 1. Сначала ищем email и сохраняем их
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        while (emailMatcher.find()) {
            String email = emailMatcher.group();
            emails.add(email);
            ownersMap.put(email, createOwnerInfo(2, email));
        }

        // 2. Markdown ссылки на Telegram
        Pattern markdownLinkPattern = Pattern.compile("\\[([^\\]]+)\\]\\((?:https?://)?t\\.me/(\\w+)\\)");
        Matcher markdownMatcher = markdownLinkPattern.matcher(text);
        while (markdownMatcher.find()) {
            String username = markdownMatcher.group(2);
            if (isValidTgContact(username)) {
                String value = "@" + username;
                ownersMap.putIfAbsent(value, createOwnerInfo(3, value));
            }
        }

        // 3. Прямые ссылки на t.me
        Pattern directTgPattern = Pattern.compile("(?:https?://)?t\\.me/(\\w+)");
        Matcher directTgMatcher = directTgPattern.matcher(text);
        while (directTgMatcher.find()) {
            String username = directTgMatcher.group(1);
            if (isValidTgContact(username)) {
                String value = "@" + username;
                ownersMap.putIfAbsent(value, createOwnerInfo(3, value));
            }
        }

        // 4. Telegram username (исключаем те, что являются частью email)
        Matcher tgMatcher = TG_USERNAME_PATTERN.matcher(text);
        while (tgMatcher.find()) {
            String username = tgMatcher.group(1);
            String fullUsername = "@" + username;

            if (!isValidTgContact(username)) {
                continue;
            }

            boolean isPartOfEmail = emails.stream().anyMatch(email -> email.contains(username));

            if (!isPartOfEmail && !ownersMap.containsKey(fullUsername)) {
                ownersMap.put(fullUsername, createOwnerInfo(3, fullUsername));
            }
        }

        // 5. Телефон
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        while (phoneMatcher.find()) {
            String value = phoneMatcher.group().trim();
            ownersMap.putIfAbsent(value, createOwnerInfo(1, value));
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