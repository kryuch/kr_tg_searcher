package ru.kryuch.krtg.searcher.service;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

    private final SettingService settingService;
    private final ChatService chatService;
    private final TelegramMessagingService telegramMessagingService;

    private static final String CRONTIME_CODE = "cron_time";
    private static final String CRON_LASTRUN_CODE = "cron_lastrun";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedDelay = 120000)
    public void schedule() {
        try {
            String cronTab = settingService.getValueByCode(CRONTIME_CODE);
            String cronLastRun = settingService.getValueByCode(CRON_LASTRUN_CODE);

            if (shouldRun(cronTab, cronLastRun)) {
                log.info("Запуск задачи по расписанию: {}", cronTab);
                doTask();
                String now = LocalDateTime.now().format(FORMATTER);
                settingService.setValueByCode(CRON_LASTRUN_CODE, now);
                log.info("Задача выполнена, обновлено время: {}", now);
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке расписания", e);
        }
    }

    private boolean shouldRun(String cronTab, String cronLastRun) {
        if (cronTab == null || cronTab.isEmpty()) {
            return false;
        }

        try {
            LocalDateTime nextExecution = getNextExecutionTime(cronTab);
            LocalDateTime now = LocalDateTime.now();

            // Если задача никогда не запускалась — запускаем сейчас
            if (cronLastRun == null || cronLastRun.isEmpty()) {
                log.info("🟢 Первый запуск задачи по расписанию: {}", cronTab);
                return true;
            }

            LocalDateTime lastRun = LocalDateTime.parse(cronLastRun, FORMATTER);

            // Проверяем: следующее выполнение уже наступило И последний запуск был до него
            if (!nextExecution.isAfter(now) && lastRun.isBefore(nextExecution)) {
                log.info("🟢 Запуск задачи по расписанию: {}, последний запуск: {}", cronTab, lastRun);
                return true;
            }

            log.debug("⏳ Ожидание следующего выполнения: {}", nextExecution);
            return false;

        } catch (Exception e) {
            log.error("Ошибка при проверке расписания", e);
            return false;
        }
    }

    private LocalDateTime getNextExecutionTime(String cronTab) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
        Cron cron = parser.parse(cronTab);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Optional<ZonedDateTime> next = executionTime.nextExecution(now);

        if (next.isPresent()) {
            return next.get().toLocalDateTime();
        } else {
            throw new IllegalStateException("Невозможно вычислить следующее время выполнения для крона: " + cronTab);
        }
    }

    private void doTask() {
        log.info("Выполнение задачи по расписанию");
        SearchParams searchParams =
                SearchParams.builder()
                        .groupType(PersonalChatType.PERSONAL)
                        .minDiffDaysCount(Integer.valueOf(settingService.getValueByCode("max_day")))
                        .botType(PersonalChatType.PERSONAL)
                        .excludeStatusFlag(true)
                        .term(settingService.getValueByCode("term"))
                        .lastMessage(settingService.getValueByCode("cron_lastmessage"))
                        .maxFoundCount(16)
                        .messagesCount(0)
                        .build();

        List<ChatInfo> chats = chatService.search(searchParams, false);

        log.info("Найдены " + chats.stream().map(ChatInfo::getName).collect(Collectors.joining(", ")));

        chats = telegramMessagingService.sendToChats(
                settingService.getValueByCode("cron_newmessage"),
                chats.stream().map(ChatInfo::getId).toList()
        );

        log.info("Сообщение отправлено в " + chats.stream().map(ChatInfo::getName).collect(Collectors.joining(", ")));
    }
}