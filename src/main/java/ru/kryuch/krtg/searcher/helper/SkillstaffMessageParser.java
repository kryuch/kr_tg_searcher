package ru.kryuch.krtg.searcher.helper;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestDto;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SkillstaffMessageParser {

    private static final Pattern HEADER =
        Pattern.compile("(?m)^[\\s\\p{So}]*Ищем\\s+(\\d+)\\s+(.+?)\\s*$");
    private static final Pattern GRADES_AND_TITLE = Pattern.compile(
        "^((?:Junior|Middle|Senior|Lead)\\+?(?:\\s*/\\s*(?:Junior|Middle|Senior|Lead)\\+?)*)\\s+(.+)$",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern URL =
        Pattern.compile("(https?://skillstaff\\.ru/actual-requests/(\\d+)/?)");
    private static final Pattern DATE_RANGE = Pattern.compile(
        "с\\s+(\\d{2}\\.\\d{2}\\.\\d{4})\\s+по\\s+(\\d{2}\\.\\d{2}\\.\\d{4})",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern DATE_UNTIL = Pattern.compile(
        "до\\s+(\\d{2}\\.\\d{2}\\.\\d{4})",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern AMOUNT = Pattern.compile("(\\d[\\d\\s\\u00A0]*(?:[.,]\\d+)?)");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<VacancySkillstaffRequestDto> parse(Message message) {
        log.info("SkillstaffMessageParser::parse (message = {})", message);
        if (message == null || message.getValue() == null) {
            return List.of();
        }
        String text = message.getValue().replace("*", "").replace("\r", "");

        List<Integer> starts = new ArrayList<>();
        Matcher header = HEADER.matcher(text);
        while (header.find()) {
            starts.add(header.start());
        }

        List<VacancySkillstaffRequestDto> result = new ArrayList<>();
        for (int i = 0; i < starts.size(); i++) {
            int end = i + 1 < starts.size() ? starts.get(i + 1) : text.length();
            parseBlock(text.substring(starts.get(i), end).trim()).ifPresent(result::add);
        }
        return result;
    }

    private Optional<VacancySkillstaffRequestDto> parseBlock(String block) {
        Matcher header = HEADER.matcher(block);
        Matcher url = URL.matcher(block);
        if (!header.find() || !url.find()) {
            log.warn("Skillstaff: блок пропущен, нет заголовка или ссылки: {}", block);
            return Optional.empty();
        }
        Matcher gradesAndTitle = GRADES_AND_TITLE.matcher(header.group(2));
        if (!gradesAndTitle.matches()) {
            log.warn("Skillstaff: блок пропущен, грейды не распознаны: {}", header.group(2));
            return Optional.empty();
        }

        VacancySkillstaffRequestDto dto = new VacancySkillstaffRequestDto();
        dto.setExternalId(url.group(2));
        dto.setUrl(url.group(1));
        dto.setTitle(gradesAndTitle.group(2).trim());
        dto.setSpecialistsCount(Integer.valueOf(header.group(1)));
        dto.setGrades(parseGrades(gradesAndTitle.group(1)));
        dto.setLocation(field(block, "Локация"));
        dto.setCitizenship(field(block, "Гражданство"));
        dto.setWorkFormat(parseWorkFormat(field(block, "Формат работы")));
        parseDuration(field(block, "Срок привлечения"), dto);
        parseRate(field(block, "Ставка"), dto);
        dto.setRawText(sanitize(block));
        return Optional.of(dto);
    }

    private static String field(String block, String label) {
        Matcher m = Pattern
            .compile("(?m)^[\\s\\p{So}]*" + Pattern.quote(label) + ":\\s*(.+?)\\s*$")
            .matcher(block);
        return m.find() ? m.group(1) : null;
    }

    private static Set<String> parseGrades(String value) {
        return Arrays.stream(value.split("/"))
            .map(grade -> grade.trim().toUpperCase(Locale.ROOT).replace("+", "_PLUS"))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private WorkFormat parseWorkFormat(String value) {
        if (value == null) {
            return null;
        }
        String v = value.toLowerCase(Locale.ROOT);
        if (v.startsWith("удал")) {
            return WorkFormat.REMOTE;
        }
        if (v.contains("гибрид")) {
            return WorkFormat.HYBRID;
        }
        if (v.contains("офис") || v.contains("территории")) {
            return WorkFormat.ON_SITE;
        }
        log.warn("Skillstaff: неизвестный формат работы: {}", value);
        return null;
    }

    private void parseDuration(String value, VacancySkillstaffRequestDto dto) {
        if (value == null) {
            return;
        }
        dto.setDurationText(sanitize(value));
        Matcher range = DATE_RANGE.matcher(value);
        if (range.find()) {
            dto.setStartDate(parseDate(range.group(1)));
            dto.setEndDate(parseDate(range.group(2)));
            return;
        }
        Matcher until = DATE_UNTIL.matcher(value);
        if (until.find()) {
            dto.setEndDate(parseDate(until.group(1)));
        }
    }

    private void parseRate(String value, VacancySkillstaffRequestDto dto) {
        if (value == null) {
            return;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.contains("открыт")) {
            dto.setRateOpen(true);
            return;
        }
        for (String part : value.split(";")) {
            BigDecimal amount = parseAmount(part);
            if (amount == null) {
                continue;
            }
            if (part.toLowerCase(Locale.ROOT).contains("нерезидент")) {
                dto.setRateNonResident(amount);
            } else {
                dto.setRateResident(amount);
            }
        }
        if (dto.getRateResident() != null || dto.getRateNonResident() != null) {
            dto.setRateCurrency(lower.contains("руб") ? "RUB" : null);
            dto.setRateUnit(lower.contains("/час") ? "HOUR" : null);
        }
    }

    private BigDecimal parseAmount(String part) {
        Matcher m = AMOUNT.matcher(part);
        if (!m.find()) {
            return null;
        }
        return new BigDecimal(m.group(1).replaceAll("[\\s\\u00A0]", "").replace(',', '.'));
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE);
        } catch (DateTimeParseException e) {
            log.warn("Skillstaff: некорректная дата: {}", value);
            return null;
        }
    }

    private String sanitize(String input) {
        if (input == null) return null;
        // Удаляем невидимые символы, которые могут ломать UTF-8
        return input
            .replaceAll("[\\u200B\\u200C\\u200D\\uFEFF]", "") // zero-width, BOM
            .replaceAll("[\\u0000-\\u001F]", "")               // control characters
            .trim();
    }
}